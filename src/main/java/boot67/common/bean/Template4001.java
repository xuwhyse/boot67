package boot67.common.bean;

import org.openfast.FieldValue;
import org.openfast.GroupValue;
import org.openfast.Message;
import org.openfast.SequenceValue;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by whyse
 * on 2017/12/15 15:22
 */
public class Template4001 {
    static String CODE_NEED = "MD002";//MD002,MD001
    /**
     * 1500;MD001 表示指数行情数据;MD002 表示股票（A、B 股）;
     * MD003 表示债券行情;MD004 表示基金行情
     * 例:MD002
     */
    public String MDStreamID;
    /**
     * 48 产品代码symbol:603917
     * 例:603917
     */
    public String SecurityID;
    /**
     *55 产品简称,中文，GBK
     */
    public byte[] Symbol;
    /**
     * 8503 当前价格成交笔数
     * 例:9718
     */
    public long NumTrades;
    /**
     * 1020 成交量，单位：张或股
     * 股票为股，基金为份，债券与回购为手，权证为100 份
     * 股票的指数交易数量单位是100 股;基金指数 100份
     * 债券指数:手
     * 例:4464196
     */
    public long TradeVolume;
    /**
     * 8504 成交金额 代码为债券（国债、企债、
     可转债）时价格*数量*10;
     * 例:124272874
     */
    public BigDecimal TotalValueTraded;
    /**
     * 140 昨收盘价
     * 例:28.19
     */
    public BigDecimal PrevClosePx;
    /**
     * 734 昨结算价 对Level-1FAST 行情不适用
     * 例:null
     */
    public BigDecimal PrevSetPx;
    /**
     * 8506 未平仓合约数量 对Level-1FAST 行情不适用
     * 例:null
     */
    public long TotalLongPosition;

    /**
     * 行情快照组件
     */
    public List<Template4001Item> MDFullGrp;
    /**
     * 8538;该字段为8位字符串，左起每位表示特定的含义，无定义则填空格。
     第1位：‘S’表示启动（开市前）时段，‘C’表示集合竞价时段，‘T’表示连续交易时段，‘B’表示休市时段，
     ‘E’表示闭市时段，‘P’表示产品停牌，‘M’表示可恢复交易的熔断时段（盘中集合竞价），
     ‘N’表示不可恢复交易的熔断时段（暂停交易至闭市），‘D’表示开盘集合竞价阶段结束到连续竞价阶段开始之前的时段（如有）。
     第2位： ‘0’表示此产品不可正常交易，‘1’表示此产品可正常交易，无意义填空格。
     第3位：‘0’表示未上市，‘1’表示已上市。
     第4位：‘0’表示此产品在当前时段不接受进行新订单申报，‘1’ 表示此产品在当前时段可接受进行新订单申
     * 例:T111
     */
    public String TradingPhaseCode;


    //===================================================================
    public String toString(){
        StringBuilder sb = new StringBuilder("MDStreamID:"+MDStreamID);
        sb.append("  SecurityID:"+SecurityID);
        sb.append("  NumTrades:"+NumTrades);
        sb.append("  TradeVolume:"+TradeVolume);
        sb.append("  TotalValueTraded:"+TotalValueTraded);
        sb.append("  PrevClosePx:"+PrevClosePx);
        sb.append("  TradingPhaseCode:"+TradingPhaseCode);

        if(MDFullGrp!=null){
            MDFullGrp.forEach(item->{
                sb.append("\r\n");
                sb.append("  MDEntryType:"+item.MDEntryType);
                sb.append("  MDEntryPx:"+item.MDEntryPx);
                sb.append("  MDEntrySize:"+item.MDEntrySize);
            });
        }
        sb.append("\r\n");
        return sb.toString();
    }

    /**
     * 这边有过滤的功能
     * @param message
     * @return
     */
    public static Template4001 getByMessage(Message message) {
        String MDStreamID = message.getString("MDStreamID");
        if(!CODE_NEED.contains(MDStreamID)){
            return null;
        }
        Template4001 tar = new Template4001();
        FieldValue fieldValue;
        tar.MDStreamID = MDStreamID;
        tar.SecurityID = message.getString("SecurityID");
//        tar.Symbol = message.getBytes("Symbol");//中文名
        fieldValue = message.getValue("NumTrades");
        if(fieldValue!=null)
            tar.NumTrades = message.getLong("NumTrades");

        fieldValue = message.getValue("TradeVolume");
        if(fieldValue!=null)
            tar.TradeVolume = message.getLong("TradeVolume");

        fieldValue = message.getValue("TotalValueTraded");
        if(fieldValue!=null)
            tar.TotalValueTraded = message.getBigDecimal("TotalValueTraded");

        fieldValue = message.getValue("PrevClosePx");
        if(fieldValue!=null)
            tar.PrevClosePx = message.getBigDecimal("PrevClosePx");
//        tar.PrevSetPx = message.getBigDecimal("PrevSetPx");//用不了
//        tar.TotalLongPosition = message.getLong("TotalLongPosition");//用不了
        fieldValue = message.getValue("TradingPhaseCode");
        if(fieldValue!=null)
            tar.TradingPhaseCode = message.getString("TradingPhaseCode");

        SequenceValue groupValue = message.getSequence("MDFullGrp");
        int len = groupValue.getLength();
        List<Template4001Item>  listItem  = new ArrayList<>(len);
        tar.MDFullGrp = listItem;

        for(int i=0;i<len;i++){
            GroupValue item = groupValue.get(i);
            Template4001Item itemTar = new Template4001Item();
            fieldValue = item.getValue("MDEntryType");
            if(fieldValue!=null)
                itemTar.MDEntryType = item.getString("MDEntryType");
            fieldValue = item.getValue("MDEntryPx");
            if(fieldValue!=null)
                itemTar.MDEntryPx = item.getBigDecimal("MDEntryPx");
            fieldValue = item.getValue("MDEntrySize");
            if(fieldValue!=null)
                itemTar.MDEntrySize = item.getLong("MDEntrySize");

//            fieldValue = item.getValue("MDEntryTime");
//            if(fieldValue!=null)
//                itemTar.MDEntryTime = item.getString("MDEntryTime");
//            itemTar.MDEntryPositionNo = item.getLong("MDEntryPositionNo");
            //-------------------
            listItem.add(itemTar);
        }
        return tar;
    }
}