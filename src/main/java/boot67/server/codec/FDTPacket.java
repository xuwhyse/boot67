package boot67.server.codec;

public class FDTPacket {
	public final static int  PKT_HEAD_SIZE = 5;			// LEAD_CODE(1) + LEN(2) + VER(1) + PACKET_NO(1) 
	public final static int  PKT_TAIL_SIZE = 1;			// TAIL_CODE(1) 
	public final static byte PKT_LEAD 	= 0x02;			// 每筆封包開頭
	public final static byte PKT_END 	= 0x03;			// 每筆封包結尾
	public final byte PKT_ZIP 	= 0x01;			// 資料壓縮
	public final static byte PKT_NOTZIP = 0x00;			// 資料未壓縮
	//128
	public final static int  PKT_COMPRESS_SIZE = 1024*10;	// 發送封包的壓縮基準
}
