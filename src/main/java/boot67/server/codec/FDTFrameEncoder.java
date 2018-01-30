package boot67.server.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import net.asdfa.msgpack.MsgPack;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FDTFrameEncoder extends MessageToByteEncoder<Object> {

	private static Logger log = LoggerFactory.getLogger(FDTFrameEncoder.class);
	private static byte byPacketNo = 0;
	
	/*
	● 每個Packet的基本格式為
	<0x02><length><ver><p_no><packet 內容><0x03>
	■ <0x02> 為Packet的leading byte。
	■ <0x03> 為Packet的end byte。
	■ <length>為2bytes的數值，為 unsigned short 的 bytes，代表 <p_ver> + <p_no> + <packet內容>的總長度
	◆ 例如 54 代表  <p_ver> + <p_no> + <packet 內容> 共有54個bytes。
	■ <p_ver> 代表該 packet 的 version，1 bytes，代表這個 packet 的版本，保留給未來擴充 packet 格式使用。Client/Server端必須依照<p_ver>
	來決定 encode/decode 的方式。目前格式版本為1。
	■ <p_no> 代表該 packet 的編號 ，1 bytes，用來偵測資料是否有遺漏，範圍為 0 到 255，總共有 256 個編號，255 的下一個編號是 ‘0’，因
	此仍有機會無法判斷出資料是否有遺漏，但機率很小。
	■ <packet內容>為一串的 bytes, 代表packet的真正資料。
	■ 一個 packet的最大長度為 65535 + 2 + 2，也就是packet 內容 + length 欄位 + 開頭結尾
	■ MessagePack Packet 內容的第一個 byte 永遠為壓縮碼，代表目前使用的壓縮方式或是不壓縮。
	*/
	
	/*
	static private volatile int iSentBytes = 0;
	public void resetCounter() {
		iSentBytes = 0;
	}
	public int getSentBytes() {
		return iSentBytes;
	}
	*/
	
	@SuppressWarnings("unchecked")
	@Override
	protected void encode(ChannelHandlerContext ctx, Object obj, ByteBuf buf) throws Exception { // (3)

		try
		{
			//MsgPackParser message = (MsgPackParser)object;
			//int datalen = message.toByteArray().length;
			//byte[] data = message.toByteArray();			
			//String message = (String)obj;
			//byte[] data = message.getBytes();
			byte[] data;
			boolean isCompressed = false;
			try {
				data = MsgPack.pack(obj);
			}
			catch(Exception e)
			{
				throw e;
			}
							
			byte[] compressedData = null;
			int compressedLen = 0,datalen = data.length + 2 + 1;
			if(data.length >= FDTPacket.PKT_COMPRESS_SIZE ) {
				isCompressed = true;				
				compressedData = CompressUtil.compress(data);
				compressedLen = compressedData.length; 
				if(compressedLen >= data.length) {
					isCompressed = false;
					compressedData = null;
				} else {
					datalen = compressedLen + 2 + 1;
				}
			}
			/*
			if(obj instanceof String) {
				data = ((String)obj).getBytes();
			} else {
				data = (byte[])obj;
			}
			*/
			

		   	byte byHead0, byHead1, byHead2;
			byHead0 = (byte)(FDTPacket.PKT_LEAD);
			byHead1 = (byte)(datalen/256);
			byHead2 = (byte)(datalen%256);
			
			// LEAD BYTE
			buf.writeByte(byHead0);
			// PACKET LEN
			buf.writeByte(byHead1);
			buf.writeByte(byHead2);
			// VER
			buf.writeByte(1);
			// PACKET NO
			buf.writeByte(byPacketNo++);
			
			if(isCompressed) {
				buf.writeByte(1);
				buf.writeBytes(compressedData);
			} else {
				buf.writeByte(0);
				buf.writeBytes(data);				
			}
			// TAIL BYTE
			buf.writeByte(FDTPacket.PKT_END);
			data = null;
			compressedData = null;
			//iSentBytes += datalen + 4;
		}
		catch(Exception e)
		{
			log.error(e.getMessage(),e);
		}
	}
}
