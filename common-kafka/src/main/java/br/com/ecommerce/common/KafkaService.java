package br.com.ecommerce.common;

import br.com.ecommerce.serialization.GsonDeserializer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;

import java.io.Closeable;
import java.time.Duration;
import java.util.Collections;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;
import java.util.regex.Pattern;

public class KafkaService<T> implements Closeable {


    private final KafkaConsumer<String, Message<T>> consumer;
    private final ConsumerFunction parse;

    public KafkaService(String groupId, String topic, ConsumerFunction parse, Class<T> type, Map<String, String> properties) {
        this(parse, groupId, type, properties);
        this.consumer.subscribe(Collections.singletonList(topic));
    }

    public KafkaService(String groupId, Pattern topic, ConsumerFunction parse, Class<T> type, Map<String, String> properties) {
        this(parse, groupId, type, properties);
        this.consumer.subscribe(topic);
    }

    private  KafkaService(ConsumerFunction parse, String groupId, Class<T> type, Map<String, String> properties) {
        this.parse = parse;
        this.consumer = new KafkaConsumer<String, Message<T>>(getProperties(groupId, type, properties));
    }


    public void run() {
        while (true) {
            var records = consumer.poll(Duration.ofMillis(100));
            if (!records.isEmpty()) {
                System.out.println(records.count() + " records found...");
                for (var record : records) {
                    try {
                        parse.parse(record);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    private Properties getProperties(String groupId, Class<T> type, Map<String, String> overrideProperties) {
        var properties = new Properties();
        properties.setProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "127.0.0.1:9092");
        properties.setProperty(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        properties.setProperty(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, GsonDeserializer.class.getName());
        properties.setProperty(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        properties.setProperty(ConsumerConfig.CLIENT_ID_CONFIG, UUID.randomUUID().toString());
        //properties.setProperty(ConsumerConfig.MAX_POLL_INTERVAL_MS_CONFIG, "1");
        properties.putAll(overrideProperties);
        return properties;
    }

    @Override
    public void close()  {
        this.consumer.close();
    }
}
