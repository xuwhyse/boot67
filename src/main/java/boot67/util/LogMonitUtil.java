package boot67.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by whyse
 * on 2017/8/11 11:16
 */
public class LogMonitUtil {

    public static Logger logger = LoggerFactory.getLogger("sensitive");
    public static Logger logMysql = LoggerFactory.getLogger("mysqlOpt");

    /**
     * 受配置开关的log输出，默认关闭
     * @param msg
     */
    public static void printLog(String msg) {
        LogMonitUtil.logger.info(msg);
    }
    public static void printMysqlLog(String msg) {
        logMysql.info(msg);
    }
}
