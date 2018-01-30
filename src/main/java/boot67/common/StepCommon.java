package boot67.common;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by whyse
 * on 2017/12/12 17:57
 */
public class StepCommon {
    /**
     * step分隔符
     */
    public static byte soh = 0x01;//start of headline标题开始;12+1
    //"8=STEP.1.0.0".getBytes()+soh
    public static byte[] head = new byte[13];
    static {
        System.arraycopy("8=STEP.1.0.0".getBytes(),0,head,0,12);
        head[12] = soh;
    }
    public static String SHType = "35=UA5302";
    public static String SZType = "35=UA5202";
    /**
     * 8=STEP.1.0.0
     */
    public static int BeginString = 8;//
    /**
     * 消息体长度（除了8，9，10 域，所有其他域长度总和）
     */
    public static int BodyLength = 9;//
    /**
     * 校验和除了10 域本
     身，对所有其他域的每个字节累加后取256
     的余数。余数不足3 位的，前补0
     140
     */
    public static int CheckSum = 10;//
    /**
     * 35=A(登录),35=5(登出)
     * 35=UA1202(心跳)
     */
    public static int MsgType = 35;//

    public static int SenderCompID = 49;//发送方代码
    public static int TargetCompID = 56;//接收方代码
    public static int MsgSeqNum = 34;//消息序号
    /**
     * 52=20101027-13:37:56
     */
    public static int SendingTime = 52;//LocalTimeStamp,消息发送时，发送方的机器时间
    /**
     * 不是必须的,消息中编码域的字符编码类型
     */
    public static int MessageEncoding = 347;//
    public static int EncryptMethod = 98;//始终为0，即不加密
    public static int HeartBtInt = 108;//心跳监测的时间间隔
    /**
     * 01:SSE_GP_MarketData_Templates.xml
     * 02:SSE_QQ_MarketData_Templates
     * 60:SSE_CSI_MarketData_Templates
     * 61:SSE_FI_CJHQ_MarketData_Templates
     * 64:SSE_FI_ZQXX_MarketData_Templates
     * 63:SSE_FI_CJMX_MarketData_Templates
     * 62:SSE_FI_QDBJ_MarketData_Templates
     * 65:SSE_HK_MarketData_Templates
     * 66:SSE_ISP_MarketData_Templates
     * 67:SSE_TRDSES_MarketData_Templates
     */
    public static final int securityType = 167;//证券类别

    static DateFormat fadeLoginFormatDate=new SimpleDateFormat("yyyyMMdd-HH:mm:ss");
    //====================================================================

    /**
     * 8=STEP.1.0.0<SOH>9=56<SOH>35=A<SOH>49=VSS<SOH>56=VDE<SOH>34=0<SOH>52=
     20101027-13:37:56<SOH>98=0<SOH>108=0<SOH>10=140<SOH>
     * 连上系统后，就发起logon,格式固定，就是时间不同
     * @return
     */
    public static Map<Integer,String> getLogon() {
        Map<Integer,String> map = new LinkedHashMap<>(10);
        map.put(MsgType,"A");
        map.put(SenderCompID,"VSS");
        map.put(TargetCompID,"VDE");
        map.put(MsgSeqNum,"0");
        String time = fadeLoginFormatDate.format(System.currentTimeMillis());
//        time = "20101027-13:37:56";//测试校验码用的
        map.put(SendingTime,time);
        map.put(EncryptMethod,"0");
        map.put(HeartBtInt,"0");
        return map;
    }

    //========================================================
    public static void main(String[] args) {
        int i = 23;
        i = i+1000;
        String str = String.valueOf(i).substring(1);
        System.err.println(str);
    }
}
