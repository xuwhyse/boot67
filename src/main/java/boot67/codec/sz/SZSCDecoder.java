package boot67.codec.sz;

import boot67.codec.ByteAndInt;
import boot67.codec.sz.templte.SZType300111;
import boot67.common.bean.Quote;
import boot67.util.LogMonitUtil;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SZSCDecoder extends ByteToMessageDecoder {
	static File fiel = new File("e:/szdata");
	static Path path = Paths.get("e:/szdata");
	static Logger logger = LoggerFactory.getLogger(SZSCDecoder.class);
	byte[] msgTypeB = new byte[4];
	byte[] bodyLengthB = new byte[4];
	//============================================================================================
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		decodeTest3();
//		byte[] bytes = "=".getBytes();
//		System.err.println((byte) '=');
	}

	private static void decodeTest3()throws Exception {
		byte[] allBytes = Files.readAllBytes(Paths.get("e:/szdata"));//e:/data.step;);mydata
		ByteBuf allBuf = Unpooled.buffer(allBytes.length);
		allBuf.writeBytes(allBytes);

		SZSCDecoder stepSCDecoder = new SZSCDecoder();
		List<Object> out = new ArrayList<>(100);
		stepSCDecoder.decode(null, allBuf, out);
	}


	@Override
	public void decode(ChannelHandlerContext arg0, ByteBuf in,
                          List<Object> out) throws Exception {
		//1.本次in是否包含头，如果包含则读取到头位置
//		byte[] field;
//		field = new byte[in.readableBytes()];
//		in.readBytes(field);
//		writeLocal(field);
//		System.err.println(new String(field));
		//---------------------------------------
		while (isOkPack(in)){
			//头+尾+checksun=12位
			in.readBytes(msgTypeB);
			int msgType = ByteAndInt.toInt(msgTypeB);
			in.readBytes(bodyLengthB);
			int bodyLength = ByteAndInt.toInt(bodyLengthB);
			ByteBuf body = Unpooled.buffer(bodyLength);
//			ByteBuf body = in.readBytes(bodyLength);//如果自己不调用release,内存泄露
			in.readBytes(body);//去掉注释后再测试一下

			//----------不验证的话---------------------
			in.skipBytes(4);
			disPatchHandle(body,out,msgType);
			//-------验证部分，耗时---------------------
//			ByteBuf byteBuf = Unpooled.buffer(bodyLength+8);
//			byteBuf.writeBytes(msgTypeB);
//			byteBuf.writeBytes(bodyLengthB);
//			byteBuf.writeBytes(body.array());
//			byte[] Checksum = SZEncodeHelper.getChecksum(byteBuf);
//			//---------------------------------
//			in.readBytes(bodyLengthB);
//			if(ByteAndInt.toInt(bodyLengthB)==ByteAndInt.toInt(Checksum)){
//				//验证通过
////				System.err.println("msgType : "+msgType);
////				System.err.println("bodyLength : "+bodyLength);
//				disPatchHandle(body,out,msgType);
//			}else{
//				LogMonitUtil.printLog("SZ行情验证不通过，将丢弃整个包处理");
//				in.skipBytes(in.readableBytes());
//				return;
//			}
		}
	}

	/**
	 * 保证当前数据流中肯定有个完整的包
	 * @param in
	 * @return
	 */
	private boolean isOkPack(ByteBuf in) {
		if(in.readableBytes()<12){
			//头和尾+校验和=12
			return false;
		}
		in.getBytes(in.readerIndex()+4,bodyLengthB);
		int bodyLength = ByteAndInt.toInt(bodyLengthB);
		int packSum = bodyLength+12;
		//--------包异常--------------
		if(bodyLength<0 || bodyLength>45000 || in.readableBytes()>100000){
			//超过45000的包将被直接丢弃
			LogMonitUtil.printLog("包字节错误，导致丢弃整包 bodyLength:"+bodyLength
			+"  readable:"+in.readableBytes());

			in.skipBytes(in.readableBytes());
			return false;
		}
		//----------------------------------
		if(in.readableBytes()>=packSum){
			return true;
		}
		return false;
	}
	/**
	 * 此处已经完成拆包粘包
	 * @param pack
	 * @param out
	 * @param msgType
	 */
	private void disPatchHandle(ByteBuf pack, List<Object> out, int msgType) {
		//只留下有用的，加快索引
		Map<Integer,Object> map = new HashMap<>(3);
		switch (msgType){
			case 300111:
				//集中竞价交易快照行情，股票，债券等
//				System.err.println("股票");
				map.put(35,5202);
				Quote quote = SZType300111.decode(pack);
				map.put(96,quote);
				break;
//			case 309011://指数/成交量统计指标快照
////				System.err.println("指数/成交量");
//				break;
//			case 309111://指数/成交量统计指标快照
////				System.err.println("指数/成交量");
//				break;
			case 390095://频道心跳
				map.put(35,-11);
//				System.err.println("频道心跳");
				break;
//			case 390019://市场实时状态
////				System.err.println("市场实时状态");
//				break;
//			case 390012://公告
////				System.err.println("公告");
//				break;
//			case 390090://快照行情频道统计消息
////				System.err.println("快照行情频道统计消息");
//				break;
//			case 1:
//				//登录信息
////				System.err.println("登录");
//				break;
			default:
				map.put(35,-1);
//				System.err.println("其他 msgType: "+msgType);
				break;
		}
		out.add(map);
	}
	public static void writeLocal(byte[] array) {
		if(fiel.canWrite()){
			try {
				Files.write(path,array, StandardOpenOption.APPEND);//写入文件
//                Files.write(path,"\r\n".getBytes(), StandardOpenOption.APPEND);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

}
