package boot67.server;

import boot67.common.FDTFields;
import boot67.server.help.Registration;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 对接lunar等行情server的server gateway分发端业务处理
 * 例：当lunar发送订阅请求是，命令传到这边，然后有这边向上订阅，收到的数据向订阅者分发
 */
public class MsgPackLiteDataServerHandler extends ChannelInboundHandlerAdapter {
	/**
	 * channel 为key,Registration(自定义结构作为客户端的client)
	 */
	public static final ConcurrentHashMap<Channel,Registration> channels = new ConcurrentHashMap<Channel,Registration>();

	private static final Logger log = LoggerFactory.getLogger(MsgPackLiteDataServerHandler.class);

	//===========================================================================
	/**
	 * 上上层接口的回调： 客户端接入时调用
	 * @param ctx
	 * @throws Exception
	 */
	@Override
	public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
		Channel incoming = ctx.channel();
		Registration registration = new Registration();
		registration.channel = incoming;
		channels.put(incoming,registration);
		log.info("[MsgPack Server] - " + incoming.remoteAddress().toString() + " has joined! , Current Count : " + channels.size());
		sendMarkets(incoming);
	}
	@Override
	public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
		Channel incoming = ctx.channel();
		channels.remove(incoming);
		log.info("[MsgPack Server] - " + incoming.remoteAddress().toString() + " has removed , Current Count : " + channels.size());
	}

	/**
	 * 不能阻塞，否则可能造成客户端读超时
	 * @param ctx
	 * @param msg
	 * @throws Exception
	 */
	public void channelRead(final ChannelHandlerContext ctx, final Object msg)
			throws Exception {
		long time = System.currentTimeMillis();
		doRead(ctx,msg);
		long def = System.currentTimeMillis()-time;
		if(def>=3000) {
			//超过7秒好像会被客户端断开
			log.info("dataServer_Read_time:" + String.valueOf(def));
		}
	}
	/**
	 * 下游客户端请求入口
	 * @param ctx
	 * @param msg
	 * @throws Exception
	 */
	private void doRead(ChannelHandlerContext ctx, Object msg) throws Exception{
		String in = null;
		if(msg instanceof String) {
			in = (String)msg;
		} else if(msg instanceof byte[]){
			in = new String((byte[])msg,"UTF-8");
		}
		if(in != null) {
			Channel channel = ctx.channel();
			Registration lst = channels.get(channel);
			if(lst == null) {
				log.info("in : [" + in + "] , " + channel.remoteAddress().toString());
				log.error("channel not found : " + in);
			}
			else {
				parseRequest(ctx,in,lst);// Add symbol to map;
			}
		}
	}
	//=====================================================================
	/**
	 * 客户端请求入口，比如订阅
	 * @param ctx
	 * @param msg
	 * @param lst
	 */
    private static void parseRequest(ChannelHandlerContext ctx, String msg, Registration lst) {
		Channel channel = ctx.channel();
		try {
			String strHash = null;
			String strDataType = null;
			String symbols = null;
			String strMarket = null;
			String bufferingSize = "0";
			if (msg != null) {
				boolean clientHeartBeat = false;
				String[] in_arr = msg.split("\\|");
				for (String str : in_arr) {
					if (str.startsWith("API=")) {
						strDataType = str.substring(4);
						if (strDataType.equals("ClientHeartBeat")) {
							clientHeartBeat = true;
						}
					}
					if (str.startsWith("Hash=")) {
						strHash = str.substring(5);
					}
					if (str.startsWith("Symbol=")) {
						symbols = str.substring(7);
					}
					if (str.startsWith("Market=")) {
						strMarket = str.substring(7);
					}
					if (str.startsWith("BufferingMS=")) {
						bufferingSize = str.substring(12);
					}
				}
				//-----------解析完毕----------------------
				if (false == clientHeartBeat) {
					String strlog = "in : [" + msg + "] , "+ channel.remoteAddress();
					// System.out.println(strlog);
					log.debug(strlog);
				}
				int endindex = msg.indexOf("|Hash=");
				if (endindex > 0) {
					String tempStr = msg.substring(0, endindex);
					int hascode = tempStr.hashCode();

					// Compare hash code
					if (hascode != Integer.parseInt(strHash)) {
						String logstr = "HashCode mismatch : " + msg
								+ " , from : " + channel.remoteAddress();
						System.out.println(logstr);
						log.warn(logstr);
						return;
					}
					if (strDataType == null) {
						String logstr = "missing API function : " + msg+ " , from : " + channel.remoteAddress();
						System.out.println(logstr);
						log.warn(logstr);
						return;
					}
					else {
						//--------------------------------------------
						if(strDataType.equals("ClientHeartBeat")){
							return;
						}
						if(symbols!=null){
							log.info(channel.remoteAddress()+" Req API : "+strDataType+" symbols strlen :"+symbols.length());
						}else{
							log.info(channel.remoteAddress()+" Req API : "+strDataType);
						}

						if ((strDataType.equals("SUBSCRIBE") || strDataType.equals("SubsTrans"))) {
							if (strDataType.equals("SUBSCRIBE") && symbols != null) {
								//请求订阅symbol入口
								lst.subscribeSymbols(symbols);
							}
							if (strDataType.equals("SUBSCRIBE") && strMarket != null) {
								lst.subscribeMarkets(strMarket);
							}
						}else if (strDataType.equals("UNSUBSCRIBE")) {
							lst.unsubscribeSymbols(symbols);
						}
					}
				} else {
					String logstr = "Missing HashCode  : " + msg + " , from : "
							+ channel.remoteAddress();
					log.warn(logstr);
				}
			}
		} catch (Exception e) {
			log.warn(e.getMessage(), e);
		}
	}
    public static void sendMarkets(Channel channel)
    {
    	ArrayList<String>lst = new ArrayList<>();
		lst.add("SH");
		lst.add("SZ");

    	HashMap<Integer,Object> map = new HashMap<Integer,Object>();
    	map.put(FDTFields.PacketType,FDTFields.WindMarkets);
    	map.put(FDTFields.ArrayOfString,lst);
    	channel.writeAndFlush(map);
    }
    
    public static void sendHeartbeat(Channel channel, int heartbeatCounter) {
    	channel.writeAndFlush(heartbeatMessagePack(heartbeatCounter));
    }
	private static HashMap<Integer, Object> heartbeatMessagePack(int heartbeatCounter) {
		HashMap<Integer, Object> map = new HashMap<Integer, Object>();
		map.put(FDTFields.PacketType, FDTFields.Heartbeat);
		map.put(FDTFields.SerialNumber,heartbeatCounter);
		return map;
	}
    
}
