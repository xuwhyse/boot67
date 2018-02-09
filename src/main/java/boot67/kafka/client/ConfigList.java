package boot67.kafka.client;

import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.Metric;
import org.apache.kafka.common.MetricName;
import org.apache.kafka.common.PartitionInfo;

import java.util.List;
import java.util.Map;

/**
 * kafka server设置的过期，即使没有处理完，也会删除
 * Created by whyse
 * on 2018/2/8 14:33
 */
public class ConfigList {
    public static void main(String[] args) {
        showList();
//        showMetrics();//不知道什么消息
    }

    /**
     * 查看kafka topic信息和partition的信息
     */
    private static void showList() {
        KafkaConsumer<String, String> consumer = Consumer.getHandCommitConsumer();
        Map<String, List<PartitionInfo>> listMap = consumer.listTopics();
        listMap.keySet().forEach(key->{
            System.err.println("topic:"+key);
            List<PartitionInfo>  list = listMap.get(key);
            list.forEach(item->{
                System.err.println("   PartitionInfo:"+item);
            });
            System.err.println("================================================" +
                    "============================================");
        });
    }

    private static void showMetrics() {
        KafkaConsumer<String, String> consumer = Consumer.getHandCommitConsumer();
        Map<MetricName, ? extends Metric> map = consumer.metrics();
        map.keySet().forEach(key->{
            System.err.println("metricKey:"+key);
            Metric metric = map.get(key);
            System.err.println("   metric:"+metric.metricValue());
        });
    }
}
