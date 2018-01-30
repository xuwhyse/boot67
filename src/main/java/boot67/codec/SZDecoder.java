package boot67.codec;

import boot67.codec.sz.SZSCDecoder;
import boot67.codec.sz.templte.SZType300111;
import boot67.common.StepCommon;
import boot67.common.bean.Quote;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

import java.util.List;
import java.util.Map;

/**
 * Created by whyse
 * on 2017/12/28 10:42
 */
public class SZDecoder {
    SZSCDecoder szscDecoder = new SZSCDecoder();

    public void decode(ByteBuf pack, Map<Integer, Object> mapTar, List<Object> out) {

//        System.err.println(new String(pack.array()));
        while(pack.readableBytes()!=0) {
            byte[] field;
            int tarIndex = pack.bytesBefore(StepCommon.soh);
            field = new byte[tarIndex];
            pack.readBytes(field);
            pack.skipBytes(1);
            String value = new String(field);//到这边都已经过滤了soh
            String[] strTemp = value.split("=");
            int tag = Integer.parseInt(strTemp[0]);
            //----------第一层step协议过滤--------------------
            if (tag == 95) {
                int bodayLength = Integer.parseInt(strTemp[1]);//value.startsWith("95=")
                pack.skipBytes(3);//过滤96=,之后的bodayLength位是二进制数据
//                field = new byte[bodayLength];
//                pack.readBytes(field);
//                doDecode(pack,mapTar);
                try {
                    szscDecoder.decode(null,pack,out);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                //--------------------------
                return;
            }
        }
    }

    /**
     * 95号里面的二进制内容
     * @param field
     * @param mapTar
     */
    private void doDecode(ByteBuf field, Map<Integer, Object> mapTar) {
        byte[] msgTypeB = new byte[4];
        field.readBytes(msgTypeB);
        int msgType = ByteAndInt.toInt(msgTypeB);

        byte[] bodyLengthB = new byte[4];
        field.readBytes(bodyLengthB);
        int bodyLength = ByteAndInt.toInt(bodyLengthB);
//        System.err.println("msgType : "+msgType);
//        System.err.println("bodyLength : "+bodyLength);
        ByteBuf body = Unpooled.buffer(bodyLength);
        field.readBytes(body);

        switch (msgType){
            case 300111:
                //集中竞价交易快照行情，股票，债券等
//				System.err.println("股票");
                Quote quote = SZType300111.decode(body);
                mapTar.put(96,quote);
                break;
//			case 309011://指数/成交量统计指标快照
////				System.err.println("指数/成交量");
//				break;
//			case 309111://指数/成交量统计指标快照
////				System.err.println("指数/成交量");
//				break;
            case 390095://频道心跳
                mapTar.put(35,-11);
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
                mapTar.put(35,-1);
//				System.err.println("其他 msgType: "+msgType);
                break;
        }
    }
}
