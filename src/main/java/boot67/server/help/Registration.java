package boot67.server.help;

import boot67.common.bean.Quote;
import io.netty.channel.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class Registration {

	static Logger logger = LoggerFactory.getLogger(Registration.class);
	/**
	 * 连接上后，Channel将绑定客户端
	 */
	public Channel channel;
	/**
	 * 根symbolList配合，辅助查询
	 */
	ConcurrentHashMap<String,Boolean> mapSymbol = new ConcurrentHashMap<>(5000);

	/**
	 * 市场订阅标志SH
	 */
	volatile boolean shSubOpen = false;
	/**
	 * 市场订阅标志SZ
	 */
	volatile boolean szSubOpen = false;
	//===================================================================================

	/**
	 * 订阅的时候连带本地缓存发送
	 * @param symbols
	 */
	public void subscribeSymbols(String symbols) {
		String[] sym_arr = symbols.split(";");
		Set<String> setTemp = new HashSet<>(sym_arr.length);
		for(String symbol : sym_arr){
			if(symbol.contains(".SH") || symbol.contains(".SZ")) {
				mapSymbol.put(symbol,true);
				setTemp.add(symbol);
			}
		}
		//--------------------------------
		if(!setTemp.isEmpty()){
			QuotesDispatcher.dispatchQuoteForSub(setTemp,this);
		}
	}

	public void unsubscribeSymbols(String symbols) {
		String[] sym_arr = symbols.split(";");
		for(String symbol : sym_arr){
			if(symbol.contains(".SH") || symbol.contains(".SZ")) {
				mapSymbol.remove(symbol);
			}
		}
	}
	/**
	 * 按市场订阅
	 * @param strMarket
	 */
	public void subscribeMarkets(String strMarket) {
		if(strMarket.length()>0) {
//			String[] market_arr = strMarket.split(";");
			strMarket = strMarket.toUpperCase();
			if(strMarket.contains("SH")){
				shSubOpen = true;
			}
			if(strMarket.contains("SZ")){
				szSubOpen = true;
			}
		}
		//-------订阅后给发送本地缓存行情--------------
		if(shSubOpen || szSubOpen){
			QuotesDispatcher.dispatchQuoteForSubMarket(shSubOpen,szSubOpen,this);
		}
	}

	/**
	 * 上海市场行情分发情况
	 * @param listQuotes
	 */
	public void sendQuotes(List<Quote> listQuotes) {
		boolean flag = false;
		for(Quote item : listQuotes){
			if(shSubOpen || mapSymbol.containsKey(item.symbol)) {
				//如果订阅了市场或者订阅该symbol
				HashMap<Integer, Object> map = HQAdapter.getMapByQuote(item);
				channel.write(map);
				flag = true;
			}
		}
		if(flag) {
			channel.flush();
		}
	}
	/**
	 * 深圳市场的分发情况
	 * @param quote
	 */
	public void sendQuote(Quote quote) {
		boolean canSend = false;
		if(szSubOpen || mapSymbol.containsKey(quote.symbol)){
			//如果订阅了市场或者订阅该symbol
			canSend = true;
		}
		if(canSend){
			HashMap<Integer, Object> map = HQAdapter.getMapByQuote(quote);
			channel.writeAndFlush(map);
		}
	}

	public void sendQuoteDirectly(Quote quote) {
		HashMap<Integer, Object> map = HQAdapter.getMapByQuote(quote);
		channel.writeAndFlush(map);
	}
}
