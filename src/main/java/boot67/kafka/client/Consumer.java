package boot67.kafka.client;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.TopicPartition;

import java.util.Arrays;
import java.util.List;
import java.util.Properties;

/*
bin/kafka-topics.sh --list --zookeeper localhost:2181
__consumer_offsets
my-replicated-topic
test
testMuti
 */
/**
 * 最新版本的offset放在kafka的“__consumer_offsets”topic上，50个partition. 老版本是放在zookeeper上的
 * offset自动以：group_id+topic+partition
 * Created by whyse
 * on 2018/2/1 14:45
 */
public class Consumer {
        public static final String KAFKA_SERVER_URL = "120.77.211.64";//120.77.211.64  localhost
        public static final int KAFKA_SERVER_PORT = 9092;
        //=====================================================================
        public static void main(String[] args) {
//            consuByAutoCommit();
            handCommit();
//            assignSub();
        }
    /**
     * 推荐使用手动提交offset.
     * consumer.commitSync();不执行的话下次可以再次消费信息
     */
    private static void handCommit() {
        KafkaConsumer<String, String> consumer = getHandCommitConsumer();
        //----------------------------
        //按照topic订阅，
//        consumer.subscribe(Arrays.asList("test"));//"test", "__consumer_offsets",testMuti
        //-----------
        //指定partitions进行订阅
        TopicPartition topicPartition = new TopicPartition("testMuti",0);
        List<TopicPartition> listTopic = Arrays.asList(topicPartition);
        //指定订阅时，多个partition不会自动balance
        consumer.assign(listTopic);//指定partition进行订阅,topic有多个partitions
//        //--------------
//        consumer.seekToBeginning(listTopic);
//        consumer.pause(listTopic);//暂停接收,poll不会收到任何数据
//        consumer.resume(listTopic);//恢复接收
        //------------------
        while (true) {
            ConsumerRecords<String, String> records = consumer.poll(100);
            if(records.isEmpty()){
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            for (ConsumerRecord<String, String> record : records) {
                System.err.printf("offset = %d, key = %s, value = %s, timestamp= %d %n",
                        record.offset(), record.key(), record.value(),record.timestamp());
            }
            if(!records.isEmpty()){
//                consumer.commitSync();//同步提交offset
            }
            //--------------------------------
//            long lastOffset = records.get(records.size() - 1).offset();
//            consumer.commitSync(Collections.singletonMap(partition, new OffsetAndMetadata(lastOffset + 1)));
            //-----------------------------------------
        }
    }

    public static KafkaConsumer<String,String> getHandCommitConsumer() {
        Properties props = new Properties();
        props.put("bootstrap.servers", KAFKA_SERVER_URL+":"+KAFKA_SERVER_PORT);//host1:port1;host2:port2…
        props.put("group.id", "test");//重要，不同的g_id可以消费所有信息
        props.put(ConsumerConfig.CLIENT_ID_CONFIG, "xmTest");
        props.put("enable.auto.commit", "false");
        props.put("auto.commit.interval.ms", "1000");
        //broker configuration中的group.min.session.timeout.ms 与 group.max.session.timeout.ms之间
        //Consumer session 过期时间,其默认值是：10000 （10 s）
        props.put("session.timeout.ms", "30000");
        //重要：latest ，从最后的数据开始获取,前面即使没有提交的也不会被消费。(适用于实时消费，前面没了就没了)
        //earliest,从提交的最新开始消费(适合必须消费)
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");//earliest,latest
        props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");

        KafkaConsumer<String, String> consumer = new KafkaConsumer<>(props);
        return consumer;
    }

    /**
     * 订阅指定分区
     */
    private static void assignSub() {
        String topic = "test";
        TopicPartition partition0 = new TopicPartition(topic, 0);
        TopicPartition partition1 = new TopicPartition(topic, 1);
//        consumer.assign(Arrays.asList(partition0));//其他一样
    }

    //不好用
    private static void consuByAutoCommit() {
        Properties props = new Properties();
        props.put("bootstrap.servers", KAFKA_SERVER_URL+":"+KAFKA_SERVER_PORT);//最好指定多个，万一有服务器故障
//        props.put(ConsumerConfig.CLIENT_ID_CONFIG, "consumer2");
        props.put("group.id", "test");//test
        props.put("enable.auto.commit", "true");//自动提交 offset
        props.put("auto.commit.interval.ms", "1000");// 每隔一秒提交

        props.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, "30000");
        props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");//key的byte 解码器
        props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        KafkaConsumer<String, String> consumer = new KafkaConsumer<>(props);
        consumer.subscribe(Arrays.asList("test"));//"test", "bar"
        while (true) {
            ConsumerRecords<String, String> records = consumer.poll(100);
            if(records.isEmpty()){
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            for (ConsumerRecord<String, String> record : records)
                System.out.printf("offset = %d, key = %s, value = %s%n", record.offset(), record.key(), record.value());
        }
    }

}
