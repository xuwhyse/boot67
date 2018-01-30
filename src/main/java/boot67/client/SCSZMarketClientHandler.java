package boot67.client;

import boot67.codec.sz.SZEncodeHelper;
import boot67.common.bean.Quote;
import boot67.server.help.QuotesDispatcher;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 每个客户端将会有这么一个对象
 * Created by whyse
 * on 2017/1/24 18:23
 */
public class SCSZMarketClientHandler extends ChannelInboundHandlerAdapter {
    public ChannelHandlerContext ctx=null;
    static Logger logger = LoggerFactory.getLogger(SCSZMarketClientHandler.class);
    AtomicLong count = new AtomicLong(0);
    //==========================================================
    @Override
    public void channelRead(final ChannelHandlerContext ctx, final Object msg) {
        Map<Integer,Object> map = (Map<Integer, Object>) msg;
        String tag35 = map.get(35).toString();
        if(tag35.equals("5202")){
            Object ob = map.get(96);
            if(ob!=null){
                Quote quote = (Quote) ob;
//                if(quote.symbol.equals("300033.SZ")) {
////                    QuotesDispatcher.dispatchQuote(quote);//正式发送quote
//                    System.err.println(quote);
//                }
                QuotesDispatcher.dispatchQuote(quote);//正式发送quote
            }
            return;//SH
        }else if(tag35.equals("-11")){
            return;//心跳
        }
//        logger.info(msg.toString());
    }
    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        ByteBuf bytes = SZEncodeHelper.getLogon();
        ctx.writeAndFlush(bytes);
    }
}
