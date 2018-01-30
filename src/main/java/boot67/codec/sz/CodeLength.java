package boot67.codec.sz;

/**
 * 二进制个字段编码的长度
 * Created by whyse
 * on 2017/12/28 16:23
 */
public class CodeLength {
    /**
     * 接收方或者发送发代码，不足20，后面补空格"   "
     */
    public static final int CompID = 20;
    /**
     * 价格 N13(4)  186400 -> 18.6400  括号里面的标识小数： /10000
     */
    public static final int Price = 8;
    /**
     * 数量 N15(2)  /100
     */
    public static final int Qty = 8;
    /**
     * 金额 N18(4) /10000
     */
    public static final int Amt = 8;
    /**
     * 消息序号
     */
    public static final int SeqNum = 8;
    /**
     * 1:true  0:false
     */
    public static final int Boolean = 2;
    public static final int Length = 4;
    /**
     * 时间戳YYYYMMDDHHMMSSsss
     */
    public static final int LocalTimeStamp = 8;
    /**
     * 重复组的个数
     */
    public static final int NumInGroup = 4;
    /**
     * 时间戳YYYYMMDD
     */
    public static final int LocalMktDate = 4;
    /**
     * 证券代码，后面补空格"   "
     */
    public static final int SecurityID = 8;
}
