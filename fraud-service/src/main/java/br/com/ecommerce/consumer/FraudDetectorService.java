package br.com.ecommerce.consumer;

import br.com.ecommerce.common.ConsumerFunction;
import br.com.ecommerce.common.KafkaService;
import br.com.ecommerce.message.Order;
import org.apache.kafka.clients.consumer.ConsumerRecord;

import java.util.Map;

public class FraudDetectorService implements ConsumerFunction<Order> {

    public static void main(String[] args) {
        var fraudDetectorService = new FraudDetectorService();
        try(var service = new KafkaService(FraudDetectorService.class.getSimpleName(),
                "ECOMMERCE_NEW_ORDER", fraudDetectorService::parse,
                Order.class,
                Map.of())) {
            service.run();
        }
    }

    @Override
    public void parse(ConsumerRecord<String, Order> record) {
        System.out.println("---------------------------------------------");
        System.out.println("Processing new order, checking for fraud ...");
        System.out.println("Key : " + record.key());
        System.out.println("Value : " + record.value());
        System.out.println("Partition : " + record.partition());
        System.out.println("Offset : " + record.offset());
        System.out.println("Timestamp : " + record.timestamp());
        System.out.println("Order processed");
    }
}