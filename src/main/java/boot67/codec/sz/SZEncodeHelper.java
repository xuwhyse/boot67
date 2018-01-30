package boot67.codec.sz;

import boot67.codec.ByteAndInt;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

/**
 * 消息头：MsgType(4) BodyLength(4)
 * 消息尾:校验和（包括消息头+消息体 每个bite转化成uint32相加%256）(4位)
 * Created by whyse
 * on 2017/12/29 9:45
 */
public class SZEncodeHelper {
    static String SenderCompID = "VSS                 ";
    static String TargetCompID = "VDE                 ";//20
    static String passWord = "111111          ";//16位
    static String DefaultApplVerID = "1.00                            ";//32
    public static void main(String[] args) {
        ByteBuf tar = getLogon();
        System.err.println(passWord.length());
    }
    //MsgType,SenderCompID,TargetCompID,HeartBtInt,Password,DefaultApplVerID
    public static ByteBuf getLogon() {
        byte[] msgType = ByteAndInt.toByteArray(1,4);
        int len = 92;//20+20+4+16+32
        byte[] BodyLength = ByteAndInt.toByteArray(len,4);

        byte[] SenderCompIDB = SenderCompID.getBytes();
        byte[] TargetCompIDB = TargetCompID.getBytes();
        byte[] HeartBtInt = ByteAndInt.toByteArray(0,4);
        byte[] passWordB = passWord.getBytes();
        byte[] DefaultApplVerIDB = DefaultApplVerID.getBytes();

        ByteBuf byteBuf = Unpooled.buffer(104);
        byteBuf.writeBytes(msgType);
        byteBuf.writeBytes(BodyLength);
        byteBuf.writeBytes(SenderCompIDB);
        byteBuf.writeBytes(TargetCompIDB);
        byteBuf.writeBytes(HeartBtInt);
        byteBuf.writeBytes(passWordB);
        byteBuf.writeBytes(DefaultApplVerIDB);

        byte[] Checksum = getChecksum(byteBuf);
        byteBuf.writeBytes(Checksum);
//        byte[] temp = byteBuf.array();
        return byteBuf;
    }

    public static byte[] getChecksum(ByteBuf byteBuf) {
        int cks=0;
        for(int i=0;i<byteBuf.readableBytes();i++){
            cks+=getUnsignedByte(byteBuf.getByte(i));
        }
        cks = cks%256;
        return ByteAndInt.toByteArray(cks,4);
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
