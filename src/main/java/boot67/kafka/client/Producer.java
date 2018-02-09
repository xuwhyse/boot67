package boot67.kafka.client;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.util.Properties;

/**
 * Created by whyse
 * on 2018/2/1 16:28
 */
public class Producer{
    //===========================================================
    public static void main(String[] args) {
        pSendTest();
    }

    private static void pSendTest() {
        KafkaProducer<String,String> producer = getProductDef();
        for(int i = 0; i < 10; i++) {
            //向topic:test  0号partition发送数据
            ProducerRecord producerRecord = new ProducerRecord<String, String>("test",0,
                    Integer.toString(i), Integer.toString(i));
            producer.send(producerRecord);
        }
        producer.close();
    }

    private static KafkaProducer<String,String> getProductDef() {
        Properties props = new Properties();
        props.put("bootstrap.servers", Consumer.KAFKA_SERVER_URL + ":" + Consumer.KAFKA_SERVER_PORT);
        props.put("client.id", "p_test");

        props.put("acks", "all");
        props.put("retries", 0);//如果请求失败，生产者会自动重试，我们指定是0次
        props.put("batch.size", 16384);//批量发送缓存数量
        props.put("linger.ms", 1);//延迟发送1ms
        props.put("buffer.memory", 33554432);//缓冲区大小

        props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");//IntegerSerializer
        props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        KafkaProducer<String,String> producer = new KafkaProducer<>(props);
        return producer;
    }

}

