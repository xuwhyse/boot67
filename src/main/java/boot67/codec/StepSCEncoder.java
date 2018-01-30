package boot67.codec;


import boot67.common.StepCommon;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

import java.util.Iterator;
import java.util.Map;

public class StepSCEncoder extends MessageToByteEncoder<Map<Integer,String>> {

	/**
	 * @param args
	 * author:xumin 
	 * 2016-10-18 下午5:13:00
	 */
	public static void main(String[] args) {
		Map<Integer,String> msg = StepCommon.getLogon();
		ByteBuf byteBuf = getMyEncodePack(msg);

		byte[] tar = new byte[byteBuf.readableBytes()];
		byteBuf.readBytes(tar);
		System.err.println(new String(tar));
	}

	@Override
	protected void encode(ChannelHandlerContext arg0, Map<Integer,String> msg, ByteBuf out)
			throws Exception {
//		System.err.println(msg);
		out.writeBytes(getMyEncodePack(msg));
	}
	/**
	 * step上交所的编码
	 * 8=STEP.1.0.0<SOH>9=56<SOH>35=A<SOH>49=VSS<SOH>56=VDE<SOH>34=0<SOH>52=
	 20101027-13:37:56<SOH>98=0<SOH>108=0<SOH>10=140<SOH>
	 * @param msg
	 * @return
	 */
	public static ByteBuf getMyEncodePack(Map<Integer,String> msg) {
		ByteBuf byteBuf = Unpooled.buffer(100);
		Iterator<Integer> iterable = msg.keySet().iterator();
		while (iterable.hasNext()){
			int key = iterable.next();
			String strTar = String.valueOf(key)+"="+msg.get(key);
			byteBuf.writeBytes(strTar.getBytes());
			byteBuf.writeByte(StepCommon.soh);
		}
//		System.err.println(byteBuf.readableBytes());

		String lengthField = "9="+byteBuf.readableBytes();
		//头部13+lengthField.length+1+尾部7，不会超过31
		ByteBuf byteBufTar = Unpooled.buffer(byteBuf.readableBytes()+22+lengthField.length());
		byteBufTar.writeBytes(StepCommon.head);
		byteBufTar.writeBytes(lengthField.getBytes());
		byteBufTar.writeByte(StepCommon.soh);
		byteBufTar.writeBytes(byteBuf);

		int count = 0;
		for(int i=0;i<byteBufTar.readableBytes();count+=byteBufTar.getByte(i++));
		count = 1000+count%256;
		String countStr = String.valueOf(count).substring(1);
//		System.err.println(countStr);

		String checkSumStr = "10="+countStr;
		byteBufTar.writeBytes(checkSumStr.getBytes());
		byteBufTar.writeByte(StepCommon.soh);
		return byteBufTar;
	}

	/**
	 * 对数据求长度
	 * @param msg
	 * @return
	 */
	private static int getMsgLength(Map<Integer, String> msg) {
		int length = 0;
//		String temp ="";
		Iterator<Integer> iterable = msg.keySet().iterator();
		while (iterable.hasNext()){
			int key = iterable.next();
			//+2:=号和分隔符
			length = length+2+String.valueOf(key).length()+msg.get(key).length();
//			temp = temp+key+"="+msg.get(key)+"#";
		}
		return length;
	}

}
