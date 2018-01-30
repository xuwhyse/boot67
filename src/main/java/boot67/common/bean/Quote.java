package boot67.common.bean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * FDTFields 解析而来
 * Created by whyse
 * on 2017/8/7 18:34
 */
public class Quote {
    private static final Logger log = LoggerFactory.getLogger(Quote.class);
    public int sourceId = 1;
    /**
     * /**
     * 1500;MD001 表示指数行情数据;MD002 表示股票（A、B 股）;
     * MD003 表示债券行情;MD004 表示基金行情
     * 例:MD002
     */
    public String mdStreamID;
    /**
     * 55
     * 例：00700.HK
     */
    public String symbol;
    /**
     * 8538;该字段为8位字符串，左起每位表示特定的含义，无定义则填空格。
     第1位：‘S’表示启动（开市前）时段，‘C’表示集合竞价时段，‘T’表示连续交易时段，‘B’表示休市时段，
     ‘E’表示闭市时段，‘P’表示产品停牌，‘M’表示可恢复交易的熔断时段（盘中集合竞价），
     ‘N’表示不可恢复交易的熔断时段（暂停交易至闭市），‘D’表示开盘集合竞价阶段结束到连续竞价阶段开始之前的时段（如有）。
     第2位： ‘0’表示此产品不可正常交易，‘1’表示此产品可正常交易，无意义填空格。
     第3位：‘0’表示未上市，‘1’表示已上市。
     第4位：‘0’表示此产品在当前时段不接受进行新订单申报，‘1’ 表示此产品在当前时段可接受进行新订单申
     * 例:T111
     *
     * //-------以下深圳----------
     * S=开始前 o=开盘集合竞价 T=连续竞价 B=休市
     C=收盘集合竞价 E=闭市 H=临时停牌 A=盘后交易 V=波动性中断; 第一位: 0=正常  1=全天停牌)
     T0
     */
    public String tradingPhaseCode;
    /**
     * 11
     * SZ,HK,US: 66:停牌  2：集合竞价
     *  SH: X(88):停牌  P(80):break O(79):开盘的时候
     */
    public int status;
    /**
     * 31，最新价
     */
    public double last;
    public double lastVol;
    /**
     * 13 当日交易额
     */
    public double turnover;
    /**
     * 14 当天总量
     */
    public double totalVolume;
    /**
     * 32
     */
    public double high;
    /**
     * 33
     */
    public double low;
    /**
     * 25
     */
    public double open;
    /**
     * 40 昨收
     */
    public double preClose;
//    /**
//     *49 涨停
//     */
//    public double highLimit;
//    /**
//     * 48 跌停
//     */
//    public double lowLimit;
    /**
     * 23 交易系统成交时间
     * 特别注意：不系统的long时间,已经格式化过
     * 111851000 : 11:18:51.000
     */
    public long timeStamp;
    /**
     * 本地接受到的时间
     */
    public long timeRec;
    /**
     * 20170808: 2017:08:08
     */
    public long tradeDate;
//    public boolean stale;
    /**
     * 18 N档买盘
     */
    public List<Double> bids;
    /**
     * 19 N档买盘对应量
     */
    public List<Long> bidVols;
    /**
     * 20 N档卖盘
     */
    public List<Double> asks;
    /**
     * 21 N档卖盘对应量
     */
    public List<Long> askVols;
    /**
     * 留给客户端上层用的保留对象
     */
    public Object tag;

    //==================================================
    public static List<Quote> getQuotesByMap(Map<Integer, Object> mapTar) {
        List<Template4001> listTemp = (List<Template4001>) mapTar.get(96);
        if(listTemp!=null && !listTemp.isEmpty()){
            List<Quote> listTar = new ArrayList<>(listTemp.size());
            long tradeDate = Long.parseLong(mapTar.get(75).toString());//20171214
            long timeStamp = Long.parseLong(mapTar.get(779).toString());//14135703
            listTemp.forEach(temp4001->{
                Quote quote = getQuoteByTemp(temp4001);
                quote.tradeDate = tradeDate;
                quote.timeStamp = timeStamp*10;//SH的数据SSS只有两位，需要*10
//                System.err.println(quote);//测试
                listTar.add(quote);
            });
            return listTar;
        }else{
//            log.error(mapTar.toString());
        }
        return null;
    }
    private static Quote getQuoteByTemp(Template4001 template4001) {
        Quote item = new Quote();
        item.mdStreamID = template4001.MDStreamID;
        item.symbol = template4001.SecurityID+".SH";//***.SH是自己加的
        item.timeRec = System.currentTimeMillis();
        item.tradingPhaseCode = template4001.TradingPhaseCode;
        if(item.tradingPhaseCode.contains("P")){
//        这是转化停牌的逻辑
            item.status = 88;
        }
        item.turnover = template4001.TotalValueTraded.doubleValue();//13
        item.totalVolume = template4001.TradeVolume;
        item.preClose = template4001.PrevClosePx.doubleValue();
        int lenItem = template4001.MDFullGrp.size()/2-2;
        item.bids = new ArrayList<>(lenItem);
        item.bidVols = new ArrayList<>(lenItem);
        item.asks = new ArrayList<>(lenItem);
        item.askVols = new ArrayList<>(lenItem);
        for(Template4001Item template4001Item: template4001.MDFullGrp){
            if(template4001Item.MDEntryPx==null){
                continue;
            }
            if(template4001Item.MDEntryType.equals("0")){
                //买盘
                item.bids.add(template4001Item.MDEntryPx.doubleValue());
                item.bidVols.add(template4001Item.MDEntrySize);
            } else if(template4001Item.MDEntryType.equals("1")){
                //卖盘
                item.asks.add(template4001Item.MDEntryPx.doubleValue());
                item.askVols.add(template4001Item.MDEntrySize);
            } else if(template4001Item.MDEntryType.equals("2")){
                //成交价
                item.last = template4001Item.MDEntryPx.doubleValue();
                item.lastVol = template4001Item.MDEntrySize;
            }else if(template4001Item.MDEntryType.equals("4")){
                item.open = template4001Item.MDEntryPx.doubleValue();
            }else if(template4001Item.MDEntryType.equals("7")){
                item.high = template4001Item.MDEntryPx.doubleValue();
            }else if(template4001Item.MDEntryType.equals("8")){
                item.low = template4001Item.MDEntryPx.doubleValue();
            }else if(template4001Item.MDEntryType.equals("5")){
                if(template4001Item.MDEntryPx!=null) {
                    item.last = template4001Item.MDEntryPx.doubleValue();
                }
            }
        }

        if(item.bids.size()!=item.bidVols.size()){
            //不匹配就丢弃
            item.bids.clear();
            item.bidVols.clear();
        }
        if(item.asks.size()!=item.askVols.size()){
            //不匹配就丢弃
            item.asks.clear();
            item.askVols.clear();
        }

        return item;
    }
    public String toString() {
        StringBuilder sb = new StringBuilder("symbol:"+symbol);
        sb.append(" tradingPhaseCode:"+tradingPhaseCode);
//        sb.append(" mdStreamID:"+mdStreamID);
//        sb.append(" status:"+status);
        sb.append(" tradeDate:"+tradeDate);
        sb.append(" timeStamp:"+timeStamp);
        sb.append(" timeRec:"+timeRec);
        sb.append(" preClose:"+preClose);
        sb.append(" open:"+open);
        sb.append(" high:"+high);
        sb.append(" low:"+low);
        sb.append(" last:"+last);
        sb.append(" totalVolume:"+totalVolume);
        sb.append(" \r\n");
        sb.append(" turnover:"+turnover);
        sb.append(" lastVol:"+lastVol);

//        sb.append(" ask:"+ask);
//        sb.append(" bid:"+bid);
        sb.append(" bids:"+bids);
        sb.append(" bidVols:"+bidVols);
        sb.append(" asks:"+asks);
        sb.append(" askVols:"+askVols);
        return sb.toString();
    }
    public Quote clone(){
        Quote temp = new Quote();
        temp.symbol = symbol;
        temp.timeStamp = timeStamp;
//        temp.ask = ask;
        temp.asks = asks;
//        temp.askVol = askVol;
        temp.askVols = askVols;
//        temp.bid = bid;
        temp.bids = bids;
//        temp.bidVol = bidVol;
        temp.bidVols = bidVols;
        temp.high = high;
        temp.last = last;
        temp.lastVol = lastVol;
        temp.low = low;
        temp.preClose = preClose;
        temp.open = open;
        temp.sourceId = sourceId;
        temp.totalVolume = totalVolume;
        temp.tradeDate = tradeDate;
        temp.timeRec = timeRec;
        temp.tag = tag;
//        temp.status = status;
        temp.turnover = turnover;
        return temp;
    }
    /**
     * 将timeStamp 转化成 HHmmss的方法
     * @return
     */
    public static String getHHmmssTs(long timeStamp) {
        String time = String.valueOf(timeStamp);
        if(time.length() == 8){
            time = "0"+time;
        }
        StringBuilder sb = new StringBuilder(time.substring(0,2));
        sb.append(":").append(time.substring(2,4)).append(":")
                .append(time.substring(4,6));
        return sb.toString();
    }

    /**
     * 根据ts的时间过滤，只要open时间的quote.
     * 根据不同业务需求，有时候盘前数据也需要，可以选择不过滤
     * A.Open: 9:30:00 - 11:30:00  13:00:00 - 15:00:00
     * HK.Open: 9:30:00 - 12:00:00 13:00:00 - 16:10:00
     * US.Open: 21:30:00 - 24:00:00 00:00:00 - 04:00:00
     *
     * 111851000 : 11:18:51.000  ts
     * 093000000
     * 213000000
     * 040000000
     * @param quote
     * @return
     */
    public static boolean filterOpen(Quote quote) {
        if(quote.symbol.contains(".SH") || quote.symbol.contains(".SZ")){
            if(quote.timeStamp >= 93000000){
                return true;
            }else{
                return false;
            }
        }
        if(quote.symbol.contains(".HK")){
            if(quote.timeStamp >= 93000000){
                return true;
            }else{
                return false;
            }
        }
        if(quote.symbol.contains(".US")){
            if(quote.timeStamp >= 213000000 || quote.timeStamp < 40200000){
                return true;
            }else{
                return false;
            }
        }
        return false;
    }

    /**
     * A股跟HK判断是否开盘的方法一样
     * @param quote
     * @return
     */
    public static boolean filterAorHKOpen(Quote quote) {
        if(quote.timeStamp >= 93000000){
            return true;
        }else{
            return false;
        }
    }

}
