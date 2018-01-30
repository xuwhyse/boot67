package boot67.codec;

public class ByteAndInt {
//	ByteBuffer bufferLong = ByteBuffer.allocate(8);
	/**
	 * @param args
	 * author:xumin 
	 * 2016-10-13 下午5:23:30
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		byte[] temp = toByteArray(9,4);
		System.err.println(toInt(temp));
	}
	/**
	 * 将int转化成指定长度的byte数组
	 * @param iSource
	 * @param iArrayLen <=4
	 * @return
	 * author:xumin 
	 * 2016-10-13 下午5:23:56
	 */
	public static byte[] toByteArray(int iSource, int iArrayLen) {
	    byte[] bLocalArr = new byte[iArrayLen];
	    for (int i = 0; (i<=4 && i < iArrayLen); i++) {
	    	int temp = iArrayLen-i-1;
	        bLocalArr[i] = (byte) (iSource >> 8 * temp & 0xFF);
	    }
	    return bLocalArr;
	}

	// 将byte数组bRefArr转为一个整数,字节数组的低位是整型的低字节位
	public static int toInt(byte[] bRefArr) {
	    int iOutcome = 0;
	    byte bLoop;
	    int length = bRefArr.length;
	    for (int i = 0; i < length; i++) {
	        bLoop = bRefArr[i];
	        int temp = length-i-1;
	        iOutcome += (bLoop & 0xFF) << (8 * temp);
	    }
	    return iOutcome;
	}

	/**
	 *  8位数组转化成long
	 * @param bytes
	 * @return
	 */
//	public long bytesToLong(byte[] bytes) {
//		bufferLong.put(bytes, 0, bytes.length);
//		bufferLong.flip();//need flip
//		return bufferLong.getLong();
//	}
	/**
	 * 将长度为2的byte数组转换为16位int
	 *
	 * @param res
	 *            byte[]
	 * @return int
	 * */
	public static int byte2int(byte[] res) {
		// res = InversionByte(res);
		// 一个byte数据左移24位变成0x??000000，再右移8位变成0x00??0000
		int targets = (res[0] & 0xff) | ((res[1] << 8) & 0xff00); // | 表示安位或
		return targets;
	}
	public static byte[] LongToBytes(long values) {
		byte[] buffer = new byte[8];
		for (int i = 0; i < 8; i++) {
			int offset = 64 - (i + 1) * 8;
			buffer[i] = (byte) ((values >> offset) & 0xff);
		}
		return buffer;
	}

	public static long BytesToLong(byte[] buffer) {
		long  values = 0;
		for (int i = 0; i < 8; i++) {
			values <<= 8; values|= (buffer[i] & 0xff);
		}
		return values;
	}
	/**
	 * 将byte -> UnsignedInt
	 * @param aByte
	 * @return
	 */
	private static int getUnsignedByte(byte aByte) {
		return aByte&0x0FF ;
	}
}
