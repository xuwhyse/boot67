package boot67;

import boot67.client.SCMarketClient;
import boot67.client.SCSZMarketClient;
import boot67.server.SCMarketServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Service;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

/**
 * 所有模块init的调用部分
 * Created by whyse
 * on 2017/9/14 13:56
 */
@Service
public class ApplicationStarter implements ApplicationListener<ContextRefreshedEvent> {

    static Logger logger = LoggerFactory.getLogger(ApplicationStarter.class);

    @Autowired
    SCMarketClient scMarketClient;
    @Autowired
    SCSZMarketClient scszMarketClient;
    @Autowired
    SCMarketServer scMarketServer;
    //==============================================================================
    //系统加载完成的事件
    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        //防止重复执行。
        if(event.getApplicationContext().getParent() == null){
            init();
        }
    }

    public int init(){
        scMarketClient.start();//启动sh，vss客户端
        scszMarketClient.start();//启动深圳vss
        scMarketServer.start();
//        startMainMonit();//启动监控线程
        return 0;
    }

    private void startMainMonit() {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                try {
                    long now = System.currentTimeMillis();
//                    检查持续了多久
//                    logger.info("主监控线程 cost time: " + (System.currentTimeMillis() - now));
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        };
        ThreadFactory threadFactory = new ThreadFactory() {
            @Override
            public Thread newThread(Runnable r) {
                Thread rd = new Thread(r);
                rd.setName("mainMonit");
                return rd;
            }
        };
        ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor(threadFactory);
        // 第二个参数为首次执行的延时时间，第三个参数为定时执行的间隔时间
        service.scheduleAtFixedRate(runnable, 30, 60, TimeUnit.SECONDS);
    }

}
