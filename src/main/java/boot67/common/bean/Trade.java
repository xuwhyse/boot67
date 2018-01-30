package boot67.common.bean;

import boot67.common.FDTFields;
import io.netty.util.CharsetUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;

/**
 * FDTFields 解析而来
 * Created by whyse
 * on 2017/8/17 14:25
 */
public class Trade {
    private static final Logger log = LoggerFactory.getLogger(Trade.class);
    /**
     * 38 成交序号,单调递增
     */
    public long indexNumber;
    /**
     * 55
     * 例：00700.HK
     */
    public String symbol;
    /**
     * 31 当前成交价
     */
    public double price;
    /**
     * 14 volume
     */
    public double volume;
    /**
     * 39
     * -1 : 卖   1：买  0：不明
     */
    public int buySellFlag;
    /**
     * 成交时间(23)
     * 特别注意：不系统的long时间,已经格式化过
     * 141801660: 14:18:01.660
     */
    public long timeStamp;
    /**
     * 系统收到时间
     */
    public long timeRec;
    /**
     * 业务扩展对象
     */
    public Object tag;

    public static Trade getTradeByMap(HashMap<Integer, Object> in) {
        int multiplier = 10000;
        if(in.get(106)!=null){
            multiplier = Integer.parseInt(in.get(FDTFields.Multipler).toString());
        }
        Trade item = new Trade();
        String symbol = new String((byte[])in.get(FDTFields.WindSymbolCode), CharsetUtil.UTF_8);
        item.symbol = symbol;

        Object temp = in.get(FDTFields.Last);
        if(temp!=null)
            item.price = Long.parseLong(temp.toString())*1.0/multiplier;

        temp = in.get(FDTFields.BuySellFlag);
        if(temp!=null) {
            int tempInt = Integer.parseInt(temp.toString());
            if(tempInt==83) {
                //S 卖
                item.buySellFlag = -1;
            }else if (tempInt==66){
                item.buySellFlag = 1;
            }else{
                item.buySellFlag = 0;
            }
        }
        temp = in.get(FDTFields.Volume);
        if(temp!=null)
            item.volume = Double.parseDouble(temp.toString());

        temp = in.get(FDTFields.IndexNumber);
        if(temp!=null)
            item.indexNumber = Long.parseLong(temp.toString());

        temp = in.get(FDTFields.Time);
        if(temp!=null)
            item.timeStamp = Long.parseLong(temp.toString());
        item.timeRec = System.currentTimeMillis();
        return item;
    }
    public String toString() {
        StringBuilder sb = new StringBuilder("symbol:"+symbol);
        sb.append(" timeStamp:"+timeStamp);
        sb.append(" timeRec:"+timeRec);
        sb.append(" indexNumber:"+indexNumber);
        sb.append(" price:"+price);
        sb.append(" volume:"+volume);
        sb.append(" buySellFlag:"+buySellFlag);
        return sb.toString();
    }
    public Trade clone(){
        Trade temp = new Trade();
        temp.symbol = symbol;
        temp.timeStamp = timeStamp;
        temp.volume = volume;
        temp.indexNumber = indexNumber;
        temp.buySellFlag = buySellFlag;
        temp.price = price;
        temp.timeRec = timeRec;
        temp.tag = tag;
        return temp;
    }

    public static void main(String[] args) {
//        long timeStamp = 110236098;
        long timeStamp = 95821000;
        System.out.println(getHHmmssTs(timeStamp));
    }

    /**
     * 将timeStamp 转化成 HHmmss的方法
     * @return
     */
    public static String getHHmmssTs(long timeStamp) {
        String time = String.valueOf(timeStamp);
        if(time.length() == 8){
            //第一位是0的话就会有点问题
            time = "0"+time;
        }
        StringBuilder sb = new StringBuilder(time.substring(0,2));
        sb.append(":").append(time.substring(2,4)).append(":")
                .append(time.substring(4,6));
        return sb.toString();
    }
}
