package boot67.server.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import net.asdfa.msgpack.MsgPack;

import java.util.List;

public class FDTFrameDecoder extends ByteToMessageDecoder {

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
	static private volatile int iReceivedBytes = 0;
	static private volatile int iPacketLen = 0;
	static private volatile int iDropBytes = 0;
	
	public static void ResetCounter() {
		iReceivedBytes = 0;
	}
	
	public static int getReceivedBytes() {
		return iReceivedBytes;
	}
	public static int getPacketLen() {
		return iPacketLen;
	}
	public static int getDropBytes() {
		return iDropBytes;
	}
	
	@Override
	protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
		while(in.readableBytes() > (FDTPacket.PKT_HEAD_SIZE + FDTPacket.PKT_TAIL_SIZE)) {
			//packet_head(5) + packet_tail(1)
		
		
			in.markReaderIndex();
			byte head_byte = in.readByte();
			if(head_byte != FDTPacket.PKT_LEAD) {	
				iReceivedBytes += 1;
				iDropBytes += 1;
				continue; //discard head byte that is not 0x02;
			}
			

			int packetlen_hi = unsignedByteToInt(in.readByte());
			int packetlen_low = unsignedByteToInt(in.readByte());
			int packetlen = packetlen_low + packetlen_hi*256;
			if(in.readableBytes() < (packetlen + FDTPacket.PKT_TAIL_SIZE)) {			
				in.resetReaderIndex(); //more data needed;
				break;
			}
			
			byte byVer = in.readByte();
			byte byPktNo = in.readByte();
			byte byIsCompressed = in.readByte();//这个是压缩码标识
			byte[] body_buf = new byte[packetlen - 3];
			in.readBytes(body_buf);
			byte tail_byte = in.readByte();
			
							
			if(tail_byte != FDTPacket.PKT_END) {
				//discard tail byte that is not 0x03, skill one byte, keep checking head byte(0x02)
				in.resetReaderIndex();
				in.readByte();
				iReceivedBytes += 1;
				iDropBytes += 1;
				continue; 
			}
			
			iPacketLen = packetlen + 4;
			iReceivedBytes += iPacketLen; 
			if(byIsCompressed == 0) {
				try {
					Object obj = MsgPack.unpack(body_buf);			
					out.add(obj);
				} catch(Exception e) {
					throw new MsgPackException("Exception occurs at Unpack normal packet");
				}
			} else {
				byte[] decompBuf = CompressUtil.decompress(body_buf);
				try {
					out.add(MsgPack.unpack(decompBuf));
				} catch(Exception e) {
					decompBuf = null;
					throw new MsgPackException("Exception occurs at Unpack compressed packet");
				}
				decompBuf = null;
			}
			body_buf = null;			
		}
	}
	
	public static int unsignedByteToInt(byte b) {  
	    return (int) b & 0xFF;  
	}  
}
