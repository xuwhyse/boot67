package boot67.codec.sz.templte;

import boot67.codec.ByteAndInt;
import boot67.common.bean.Quote;
import boot67.util.DoubleUtils;
import io.netty.buffer.ByteBuf;

import java.util.ArrayList;

/**
 * Created by whyse
 * on 2017/12/28 16:47
 */
public class SZType300111 {
    /*
     msgType(4)  OrigTime(时间 8) ChannelNo(频道代码 2) MDStreamID(行情类别  3)[010现货  020 030 040]
     SecurityID (证券代码 8 )
     SecurityIDSource(证券代码源 4 102=深圳 103=香港)
     tradingPhaseCode(8B  第0位：S=开始前 o=开盘集合竞价 T=连续竞价 B=休市
     C=收盘集合竞价 E=闭市 H=临时停牌 A=盘后交易 V=波动性中断; 第一位: 0=正常  1=全天停牌)
     prevClosePx(昨收价 8 N13(4))  numTrades(成交笔数 8) totalVolumeTrade(成交总量 8 N15(2))
     totalValueTrade(成交总金额 8  N18(4))
     extendFields(各业务扩展字段)

     noMDEntries(行情条目数 4)
         mdEntryType(2): 0=买 1=卖  2=最近价 4=开盘价 7=最高价 8=最低价....xe=涨停价 xf=跌停价
         mdEntryPx(8 n18(6)) :价格
         mdEntrySize(8 N15(2)) :数量
         mdPriceLevel(2): 买卖盘档位
         NumberOfOrders(8): 价位总委托笔数 0标识不揭示
         noOrders(4 group)
             orderQty(8 N15(2)) 委托数量
     */
    public static Quote decode(ByteBuf body) {
        Quote quote = new Quote();
        long OrigTime = getMyLong(body,8);//yyyymmddhhmmssSSS
        quote.timeStamp = OrigTime%1000000000;
        quote.tradeDate = OrigTime/1000000000;
        quote.timeRec = System.currentTimeMillis();

//        int ChannelNo = getMyShort(body,2);//62723;
//        String MDStreamID = getMyString(body,3);//010(现货);
        body.skipBytes(5);//暂时不用

        String SecurityID = getMyString(body,8);//002139
        quote.symbol = SecurityID+".SZ";//".SZ"

//        String SecurityIDSource = getMyString(body,4);//102
        body.skipBytes(4);//暂时不用

        quote.tradingPhaseCode = getMyString(body,8);//T0
        if(quote.tradingPhaseCode.contains("H") || quote.tradingPhaseCode.contains("1")){
            //深圳停牌逻辑
            quote.status = 66;
        }

//        prevClosePx(昨收价 8 N13(4))  numTrades(成交笔数 8 N15(2)) totalVolumeTrade(成交总量 8 N15(2))
//        totalValueTrade(成交总金额 8  N18(4))
        double prevClosePx = getMyDouble(body,8,10000,3);//11.58
        quote.preClose = prevClosePx;

//        long numTrades = getMyLongQty(body,8,100);//40
//        quote.lastVol = numTrades;
        body.skipBytes(8);//暂时不用

        long totalVolumeTrade = getMyLongQty(body,8,100);//3425562
        quote.totalVolume = totalVolumeTrade;
        double totalValueTrade = getMyDouble(body,8,10000,3);//4.0101E7
        quote.turnover = totalValueTrade;
//        noMDEntries(行情条目数 4)
//          mdEntryType(2): 0=买 1=卖  2=最近价 4=开盘价 7=最高价 8=最低价....xe=涨停价 xf=跌停价
        //x1=升跌一 x2=升跌二  x5=股票市盈率一 x6=股票市盈率二
//          mdEntryPx(8 n18(6)) :价格
//          mdEntrySize(8 N15(2)) :数量 qty
//          mdPriceLevel(2): 买卖盘档位
//          NumberOfOrders(8): 价位总委托笔数 0标识不揭示
//          noOrders(4 group)
//              orderQty(8 N15(2)) 委托数量
        long noMDEntries = getMyInt(body,4);//19
        //--------------------
        quote.bids = new ArrayList<>(10);
        quote.bidVols = new ArrayList<>(10);
        quote.asks = new ArrayList<>(10);
        quote.askVols = new ArrayList<>(10);
        //--------------------
        for(int i=0;i<noMDEntries;i++){
            //这边有涨停价和跌停价
            String mdEntryType = getMyString(body,2);//有可能不是数字，以后
            double mdEntryPx = getMyDouble(body,8,1000000,3);//11.78
            long mdEntrySize = getMyLongQty(body,8,100);
            if(mdEntryType.equals("2")){
                quote.last = mdEntryPx;
                quote.lastVol = mdEntrySize;
            }else if(mdEntryType.equals("4")){
                quote.open = mdEntryPx;
            }else if(mdEntryType.equals("7")){
                quote.high = mdEntryPx;
            }else if(mdEntryType.equals("8")){
                quote.low = mdEntryPx;
            }else if(mdEntryType.equals("0")){
                //0=买
                quote.bids.add(mdEntryPx);
                quote.bidVols.add(mdEntrySize);
            }else if(mdEntryType.equals("1")){
                //1=卖
                quote.asks.add(mdEntryPx);
                quote.askVols.add(mdEntrySize);
            }

//            int mdPriceLevel = getMyShort(body,2);//买卖盘档位
//            long NumberOfOrders = getMyLong(body,8);//基本=0
            body.skipBytes(10);//暂时不用

            long noOrders = getMyInt(body,4);//基本=0
            if(noOrders!=0){
                //这个字段暂时不会用，过滤掉
                body.skipBytes((int) (noOrders*8));
            }
        }
        if(quote.bids.size()!=quote.bidVols.size()){
            quote.bids.clear();
            quote.bidVols.clear();
        }
        if(quote.asks.size()!=quote.askVols.size()){
            quote.asks.clear();
            quote.askVols.clear();
        }
        return quote;
    }


    /**
     * 针对的是int64的
     * @param body
     * @param blxs 保留位数
     *@param len
     * @param xsd   @return
     */
    private static double getMyDouble(ByteBuf body,int len, int xsd,int blxs) {
        byte[] temp = new byte[len];
        body.readBytes(temp);
        long intT = ByteAndInt.BytesToLong(temp);
        return DoubleUtils.round(intT*1.0/xsd,blxs);
    }

    private static String getMyString(ByteBuf body, int len) {
        byte[] temp = new byte[len];
        body.readBytes(temp);
        return new String(temp).trim();
    }

    private static long getMyInt(ByteBuf body, int len) {
        byte[] temp = new byte[len];
        body.readBytes(temp);
        return ByteAndInt.toInt(temp);
    }

    private static int getMyShort(ByteBuf body, int len) {
        byte[] temp = new byte[len];
        body.readBytes(temp);
        return ByteAndInt.byte2int(temp);
    }
    private static long getMyLongQty(ByteBuf body, int len, int cs) {
        byte[] temp = new byte[len];
        body.readBytes(temp);
        return ByteAndInt.BytesToLong(temp)/cs;
    }
    private static long getMyLong(ByteBuf body, int len) {
        byte[] temp = new byte[len];
        body.readBytes(temp);
        return ByteAndInt.BytesToLong(temp);
    }
}
