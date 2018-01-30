package boot67.codec;

import boot67.common.StepCommon;
import io.netty.buffer.ByteBuf;

import java.util.Map;

/**
 * Created by whyse
 * on 2017/12/28 10:42
 */
public class SHDecoder {
    FastCSDecoder fastCSDecoder = new FastCSDecoder();

    public void decode(ByteBuf pack, Map<Integer, Object> mapTar) {
        boolean fixtFlag = false;
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
            if (!fixtFlag && tag == 96) {
                //step协议里面的FIXT协议开始
                fixtFlag = true;//value.startsWith(HEAD_FIXT)
                continue;
            }
            //---------------------------------------------------
            if (fixtFlag) {
                //step协议里面的FIXT协议开始
//                mapTar.put(tag, strTemp[1]);
                if (tag == 35) {
                    if (strTemp[1].equals("W")) {
						continue;
                    } else if (strTemp[1].equals("h")) {
//                        LogMonitUtil.printLog("!!!市场状态：" + new String(pack.array()));
                        return;
                    } else {
//						LogMonitUtil.printLog("过滤行情类型："+value);
//                        out.add(mapTar);
                        return;
                    }
                }else{
                    mapTar.put(tag, strTemp[1]);
                }
                if (tag == 95) {
                    int bodayLength = Integer.parseInt(strTemp[1]);//value.startsWith("95=")
                    pack.skipBytes(3);//过滤96=,之后的bodayLength位是二进制数据
                    field = new byte[bodayLength];
                    pack.readBytes(field);
                    //--------------------------------------
                    fastCSDecoder.fastDecode(field, mapTar);
                    //--------------------------
//					List<Quote> list = Quote.getQuotesByMap(mapTar);
//					if(list!=null) {
//						list.forEach(item -> {
//							System.err.println(item);
//						});
//					}
                    //--------------------------
                    return;
                }

            }
            //------------if(fixtFlag)---end-----------------------------------------
        }
    }
}
