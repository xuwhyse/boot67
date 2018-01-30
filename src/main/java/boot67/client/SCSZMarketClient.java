package boot67.client;

import boot67.codec.sz.SZSCDecoder;
import boot67.util.LogMonitUtil;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.handler.timeout.IdleStateHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.net.ssl.SSLException;

/**
 * 对接上海股票行情和深圳股票行情的
 */
@Service
public class SCSZMarketClient {

	static Logger logger = LoggerFactory.getLogger(SCSZMarketClient.class);

	@Value("${vde.sz.ip}")
	String HOST= "127.0.0.1";
	@Value("${vde.sz.port}")
	int PORT = 6666;//9129,9130,8888,(6666深圳)
	SCSZMarketClient scszMarketClient;
	public ChannelFuture channelFuture;
	long lastStartTime = 0;
	/**
	 * 0:关闭   1：连线
	 */
	public volatile int clientState = 0;
	EventLoopGroup group;
	//===================================================
	/**
	 * author:xumin 
	 * 2016-3-30 下午1:41:54
	 * @throws SSLException 
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		SCSZMarketClient scMarketClient = new SCSZMarketClient();
		scMarketClient.start();
//		Thread.sleep(7000);
//		scMarketClient.stop();
//		Thread.sleep(3000);
//		scMarketClient.start();
	}

	public void start() {
		Runnable runnable = new Runnable() {
			@Override
			public void run() {
				try {
					Thread.sleep(1000);
					synchronized (this) {
						if(clientState==1){
							logger.error("多线程启动错误！本客户端已经启动！！！");
							return;
						}
						stop();
						if (System.currentTimeMillis() - lastStartTime < 7000) {
							Thread.sleep(7000);
						}
						init();
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		};
		Thread td = new Thread(runnable);
		td.setName("vde_sz_client");
		td.start();
	}
	public void stop() {
		try {
			if (channelFuture != null) {
				channelFuture.channel().close();
			}
		}catch (Exception e){
			e.printStackTrace();
		}
	}

	private void init() throws Exception{
        // Configure the client.
		scszMarketClient = this;
		lastStartTime = System.currentTimeMillis();
		if(group==null) {
			group = new NioEventLoopGroup();
		}
        try {
            Bootstrap b = new Bootstrap();
            b.group(group).channel(NioSocketChannel.class)
					.option(ChannelOption.SO_KEEPALIVE, true)
					.option(ChannelOption.TCP_NODELAY, true)
					.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 3000)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    public void initChannel(SocketChannel ch) throws Exception {
                        ChannelPipeline p = ch.pipeline();
//                       p.addLast(new LengthFieldBasedFrameDecoder(1024*100, 0 ,
//						4, 4, 0));//最后那个是偏移量
						p.addLast(new SZSCDecoder());
						p.addLast(new IdleStateHandler(12,
								0, 0));
						p.addLast(new HeartBeatHandlerSZ(scszMarketClient));
						p.addLast(new SCSZMarketClientHandler());
                    }
                });
            // Start the client.
			channelFuture = b.connect(HOST, PORT).sync();
			clientState = 1;
			logger.info("客户端start host: "+HOST+"  port: "+PORT);
            // Wait until the connection is closed.
			channelFuture.channel().closeFuture().sync();
        }catch (Exception e){
			e.printStackTrace();
		} finally {
            // Shut down the event loop to terminate all threads.
			clientState = 0;
//            group.shutdownGracefully();
			Thread.sleep(3000);
			scszMarketClient.start();
        }
	}

}

class HeartBeatHandlerSZ extends ChannelDuplexHandler {
	private static final Logger log = LoggerFactory.getLogger(HeartBeatHandler.class);
	private SCSZMarketClient client ;

	public HeartBeatHandlerSZ(SCSZMarketClient client) {
		this.client = client;
	}

	@Override
	public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
		if (evt instanceof IdleStateEvent) {
			IdleStateEvent e = (IdleStateEvent) evt;
			if (e.state() == IdleState.READER_IDLE) {
				LogMonitUtil.printLog("连接读超时,主动断开: "+client.HOST+"  port: "+client.PORT);
				ctx.close();
				client.clientState = 0;
				client.start();
			}
//			if (e.state() == IdleState.WRITER_IDLE) {
////            	log.info("------req-----API=ClientHeartBeat---------------");
//				ctx.channel().writeAndFlush(MsgPackLiteDataClient.addHashTail("API=ClientHeartBeat",true));
//			}
		}
	}
}


