package boot67.kafka.stream;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.Topology;

import java.util.HashMap;
import java.util.Map;

/**
 *
 允许从输入topic转换数据流到输出topic。
 除了Kafka Streams，还有Apache Storm和Apache Samza可选择。
 * Created by whyse
 * on 2018/2/1 16:26
 */
public class StreamTest {
    static String url = "120.77.211.64:9092";
    public static void main(String[] args) {
        transTo();
//        consumerDo("test");
    }

    private static void consumerDo(String topic) {
        StreamsConfig config = getStreamConfig(url);
        //新建一个consumer任务
        StreamsBuilder builder = new StreamsBuilder();
        builder.stream(topic).mapValues(v->{
            System.err.println(v);
            return 1;
        });
        Topology topology = builder.build();

        KafkaStreams streams = new KafkaStreams(topology, config);
        streams.start();
    }

    /**
     * 将test的数据经过代码处理后发送到另外一个topic。
     * 将中间这一部分代码复杂，就变成数据流拓扑处理
     */
    private static void transTo() {
        StreamsConfig config = getStreamConfig(url);
        //新建一个consumer任务
        StreamsBuilder builder = new StreamsBuilder();
        builder.stream("test").mapValues(v->{
            return "new_"+v;
        }).to("testMuti");
        Topology topology = builder.build();//创建一个任务

        KafkaStreams streams = new KafkaStreams(topology, config);
        streams.start();
    }




    private static StreamsConfig getStreamConfig(String url) {
        Map<String, Object> props = new HashMap<>();
        // 指定一个应用ID，会在指定的目录下创建文件夹，里面存放.lock文件
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, "xumin-stream-test");
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, url);// 指定kafka集群
        // key 序列化 / 反序列化
        props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass());
        // value 序列化 / 反序列化
        props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass());
        StreamsConfig config = new StreamsConfig(props);
        return config;
    }
}
