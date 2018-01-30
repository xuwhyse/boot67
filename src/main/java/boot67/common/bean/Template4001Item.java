package boot67.common.bean;

import java.math.BigDecimal;

/**
 * Created by whyse
 * on 2017/12/15 16:19
 */
public class Template4001Item {
    /**
     * 269 行情条目类别
     * 0＝买入价（270，271，290）
     * 1＝卖出价（270，271，290）
     2＝成交价（270，271 和273）
     3＝指数(270，273)
     4＝今开盘价（270）
     5＝今收盘价（270）
     6＝今结算价（270）
     7＝当日最高成交价（270）
     8＝当日最低成交价（270）
     v＝IOPV（270）
     * 例:1
     */
    public String MDEntryType;
    /**
     * 行情条目价格
     * 例:28.2
     */
    public BigDecimal MDEntryPx;
    /**
     * 行情条目数量，单位：张或股
     * 例:60134
     */
    public long MDEntrySize;
    /**
     * 行情条目时间 HHMMSSss
     * 例:14135439
     */
    public String MDEntryTime;
    /**
     * 行情条目档位
     * 例:null
     */
    public long MDEntryPositionNo;
}
