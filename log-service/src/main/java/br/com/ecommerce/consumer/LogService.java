package br.com.ecommerce.consumer;

import br.com.ecommerce.common.Message;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.serialization.StringDeserializer;

import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.regex.Pattern;

public class LogService implements ConsumerFunction<String> {

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        var logService = new LogService();
        try (var service = new KafkaService(
                "ECOMMERCE_SEND_EMAIL",
                Pattern.compile("ECOMMERCE.*"),
                logService::parse,
                Map.of(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName()))){
            service.run();
        }
    }


    @Override
    public void parse(ConsumerRecord<String, Message<String>> record) {
        System.out.println("---------------------------------------------");
        System.out.println("Processing new order, checking for fraud ...");
        System.out.println("Topic : " + record.topic());
        System.out.println("Key : " + record.key());
        System.out.println("Value : " + record.value());
        System.out.println("Partition : " + record.partition());
        System.out.println("Offset : " + record.offset());
        System.out.println("Timestamp : " + record.timestamp());
    }
}
