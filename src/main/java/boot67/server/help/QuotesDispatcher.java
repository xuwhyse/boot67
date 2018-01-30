package boot67.server.help;

import boot67.common.bean.Quote;
import boot67.server.MsgPackLiteDataServerHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Set;
import java.util.concurrent.*;

/**
 * Created by whyse
 * on 2018/1/10 11:24
 */
public class QuotesDispatcher {
    static Logger logger = LoggerFactory.getLogger(QuotesDispatcher.class);
    /**
     * 保留最近一次行情的数据，当新的连接过来订阅的时候发送缓存数据给新连接
     */
    static ConcurrentHashMap<String,Quote> mapGlobalSH = new ConcurrentHashMap<>(3000);
    static ConcurrentHashMap<String,Quote> mapGlobalSZ = new ConcurrentHashMap<>(3000);
    static BlockingQueue consumerQueue;
    static ThreadPoolExecutor threadPoolExecutor;
    static {
        consumerQueue = new ArrayBlockingQueue(100);
        ThreadFactory tf = new ThreadFactory() {
            @Override
            public Thread newThread(Runnable r) {
                Thread td = new Thread(r);
                td.setName("quote_consumer_help");
                return td;
            }
        };
        /**
         * 单个线程任务池，能保证队列任务的有效性
         */
        threadPoolExecutor = new ThreadPoolExecutor(1, 1,
                60 * 60, TimeUnit.SECONDS, consumerQueue, tf, new RejectedExecutionHandler() {
            @Override
            public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
                logger.error("消费队列满 consumerQueue :"+consumerQueue.size());
            }
        });

    }
    //----------------------------------------------------------------------------
    public static void dispatchQuotes(List<Quote> listQuote) {
//        Runnable run = new Runnable() {
//            @Override
//            public void run() {
//                listQuote.forEach(item->{
//                    mapGlobalSH.put(item.symbol,item);
//                });
//                MsgPackLiteDataServerHandler.channels.values().forEach(item->{
//                    item.sendQuotes(listQuote);
//                });
//            }
//        };
//        threadPoolExecutor.execute(run);
        //--------------------------------------------------------------
        MsgPackLiteDataServerHandler.channels.values().forEach(item->{
            item.sendQuotes(listQuote);
        });
        listQuote.forEach(item->{
            mapGlobalSH.put(item.symbol,item);
        });
    }
    public static void dispatchQuote(Quote quote) {
        MsgPackLiteDataServerHandler.channels.values().forEach(item->{
            item.sendQuote(quote);
        });
        mapGlobalSZ.put(quote.symbol,quote);
    }

    public static void dispatchQuoteForSub(Set<String> setTemp, Registration registration) {
        Runnable run = new Runnable() {
            @Override
            public void run() {
                setTemp.forEach(symbol->{
                    Quote quote = mapGlobalSH.get(symbol);
                    if(quote==null){
                        quote = mapGlobalSZ.get(symbol);
                    }
                    if(quote!=null) {
                        registration.sendQuoteDirectly(quote);
                    }
                });
            }
        };
        threadPoolExecutor.execute(run);
    }

    public static void dispatchQuoteForSubMarket(boolean shSubOpen, boolean szSubOpen, Registration registration) {
        Runnable run = new Runnable() {
            @Override
            public void run() {
                if(shSubOpen){
                    mapGlobalSH.values().forEach(item->{
                        registration.sendQuoteDirectly(item);
                    });
                }
                if(szSubOpen){
                    mapGlobalSZ.values().forEach(item->{
                        registration.sendQuoteDirectly(item);
                    });
                }
            }
        };
        threadPoolExecutor.execute(run);
    }
}
