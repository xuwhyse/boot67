package boot67.server;

import boot67.server.codec.FDTFrameDecoder;
import boot67.server.codec.FDTFrameEncoder;
import boot67.util.LogMonitUtil;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.handler.timeout.IdleStateHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.net.ssl.SSLException;

/**
 * 对接上海股票行情和深圳股票行情的
 */
@Service
public class SCMarketServer {

	static Logger logger = LoggerFactory.getLogger(SCMarketServer.class);

	private int serverPort = 10048;
	SCMarketServer scMarketServer;
	public ChannelFuture channelFuture;
	long lastStartTime = 0;
	EventLoopGroup bossGroup,workerGroup;
	//===================================================
	/**
	 * author:xumin 
	 * 2016-3-30 下午1:41:54
	 * @throws SSLException 
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		SCMarketServer scMarketServer = new SCMarketServer();
		scMarketServer.start();
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
					init();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		};
		Thread td = new Thread(runnable);
		td.setName("newA_server");
		td.start();
	}

	private void init() throws Exception{
        // Configure the client.
		scMarketServer = this;
		lastStartTime = System.currentTimeMillis();
		if(bossGroup==null) {
			bossGroup = new NioEventLoopGroup();
		}
		if(workerGroup==null) {
			workerGroup = new NioEventLoopGroup();
		}
        try {
			ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup,workerGroup).channel(NioServerSocketChannel.class)
					.childOption(ChannelOption.SO_KEEPALIVE, true)
					.childOption(ChannelOption.TCP_NODELAY, true)
					.childOption(ChannelOption.CONNECT_TIMEOUT_MILLIS, 3000)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    public void initChannel(SocketChannel ch) throws Exception {
                        ChannelPipeline p = ch.pipeline();
						p.addLast(new FDTFrameDecoder());
						p.addLast(new FDTFrameEncoder());
						p.addLast(new IdleStateHandler(12,
								5, 0));
						p.addLast(new HeartBeatHandler());
						p.addLast(new MsgPackLiteDataServerHandler());
                    }
                });

            // Start the client.
			channelFuture = b.bind(serverPort).sync();
			if(channelFuture.isSuccess()) {
				logger.info("服务端start serverPort: "+serverPort);
				channelFuture.channel().closeFuture().sync();
			}
        }catch (Exception e){
			e.printStackTrace();
		}
        finally {
            // Shut down the event loop to terminate all threads.
			bossGroup.shutdownGracefully();
			workerGroup.shutdownGracefully();
        }
	}

}

class HeartBeatHandler extends ChannelDuplexHandler {
	private static final Logger log = LoggerFactory.getLogger(HeartBeatHandler.class);
	private int heartbeatCounter = 0;

	@Override
	public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
		if (evt instanceof IdleStateEvent) {
			IdleStateEvent e = (IdleStateEvent) evt;
			if (e.state() == IdleState.READER_IDLE) {
				//上游不发过来任何东西，就会超时
				LogMonitUtil.printLog("server Read timeout for " + 12 + " seconds , close client : " + ctx.channel().remoteAddress());
				ctx.close();
			}
			if (e.state() == IdleState.WRITER_IDLE) {
//            	log.info("------req-----API=ClientHeartBeat---------------");
				MsgPackLiteDataServerHandler.sendHeartbeat(ctx.channel(),++heartbeatCounter);
			}
		}
	}
}


