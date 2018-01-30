package boot67.server.help;


import boot67.common.FDTFields;
import boot67.common.bean.Quote;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by whyse
 * on 2018/1/10 13:13
 */
public class HQAdapter {
    static int multiplier = 10000;


    public static HashMap<Integer,Object> getMapByQuote(Quote quote) {
        HashMap<Integer,Object>  map = new HashMap<>(18);
        map.put(FDTFields.PacketType,FDTFields.WindMarketData);//这个map是A股的

        map.put(106,multiplier);
        map.put(FDTFields.WindSymbolCode,quote.symbol.getBytes());

        map.put(FDTFields.Status,quote.status);//这个是停牌逻辑下发

        long tempL = (long) (quote.last*multiplier);
        map.put(FDTFields.Last,tempL);

        tempL = (long) (quote.turnover*multiplier);
        map.put(FDTFields.Turnover,tempL);

        tempL = (long) (quote.totalVolume*multiplier);
        map.put(FDTFields.Volume,tempL);
//        map.put(FDTFields.Status,quote.totalVolume);

        tempL = (long) (quote.high*multiplier);
        map.put(FDTFields.High,tempL);

        tempL = (long) (quote.low*multiplier);
        map.put(FDTFields.Low,tempL);

        tempL = (long) (quote.open*multiplier);
        map.put(FDTFields.Open,tempL);

        tempL = (long) (quote.preClose*multiplier);
        map.put(FDTFields.PreClose,tempL);

        List<Long> listT = new ArrayList<>(quote.bids.size());
        for(int i=0;i<quote.bids.size();i++){
            long item = (long) (quote.bids.get(i)*multiplier);
            listT.add(item);
        }
        map.put(FDTFields.BidPriceArray,listT);
        map.put(FDTFields.BidVolumeArray,quote.bidVols);


        listT = new ArrayList<>(quote.asks.size());
        for(int i=0;i<quote.asks.size();i++){
            long item = (long) (quote.asks.get(i)*multiplier);
            listT.add(item);
        }
        map.put(FDTFields.AskPriceArray,listT);
        map.put(FDTFields.AskVolumeArray,quote.askVols);


        map.put(FDTFields.TradingDay,quote.tradeDate);
        map.put(FDTFields.ActionDay,quote.tradeDate);
        map.put(FDTFields.Time,quote.timeStamp);

        return map;
    }
}
