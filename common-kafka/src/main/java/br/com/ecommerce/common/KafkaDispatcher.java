package br.com.ecommerce.common;

import br.com.ecommerce.serialization.GsonSerializer;
import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;

import java.io.Closeable;
import java.util.Properties;
import java.util.concurrent.ExecutionException;

public class KafkaDispatcher<T> implements Closeable {

    private final KafkaProducer<String, Message<T>> producer;

    public KafkaDispatcher() {
        this.producer = new KafkaProducer<String, Message<T>>(properties());
    }

    public void send(String topic, String key, CorrelationId id, T payload) throws ExecutionException, InterruptedException {
        var value = new Message<>(id, payload);
        var record = new ProducerRecord<>(topic, key, value);
        Callback callback = (data, ex) -> {
            if (ex != null) {
                ex.printStackTrace();
            }
            System.out.println("Success: " + " topic: " + data.topic() + " | offset: " + data.partition() + " | timestamp: " + data.timestamp());
        };
        producer.send(record, callback).get();
    }

    private Properties properties() {
        var properties = new Properties();
        properties.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "127.0.0.1:9092");
        properties.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        properties.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, GsonSerializer.class.getName());
        properties.setProperty(ProducerConfig.ACKS_CONFIG, "all");
        return properties;
    }

    @Override
    public void close()  {
        this.producer.close();
    }
}
