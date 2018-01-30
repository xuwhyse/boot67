package boot67.client;

import boot67.common.StepCommon;
import boot67.common.bean.Quote;
import boot67.server.help.QuotesDispatcher;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 每个客户端将会有这么一个对象
 * Created by whyse
 * on 2017/1/24 18:23
 */
public class SCMarketClientHandler extends ChannelInboundHandlerAdapter {
    public ChannelHandlerContext ctx=null;
    static Logger logger = LoggerFactory.getLogger(SCMarketClientHandler.class);
    AtomicLong count = new AtomicLong(0);
    //==========================================================
    @Override
    public void channelRead(final ChannelHandlerContext ctx, final Object msg) {
        Map<Integer,Object> map = (Map<Integer, Object>) msg;
        String tag35 = map.get(35).toString();
        if(tag35.equals("5302")){
            Object ob = map.get(96);
            if(ob!=null){
                List<Quote> listQuote = (List<Quote>) ob;
                QuotesDispatcher.dispatchQuotes(listQuote);//正式发送quotes
//                listQuote.forEach(item->{
//                    if(item.symbol.equals("600050"))
//                    System.err.println(item);
//                });
            }
            return;//SH
        }
//        else if(tag35.equals("5202")){
//            Object ob = map.get(96);
//            if(ob!=null){
//                Quote quote = (Quote) ob;
//                System.err.println(quote);
//            }
//            return;//SZ
//        }else if(tag35.equals("-11")){
//            return;//心跳
//        }
//        logger.info(msg.toString());
    }
    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        Map<Integer,String> map = StepCommon.getLogon();
        ctx.writeAndFlush(map);
    }
}
