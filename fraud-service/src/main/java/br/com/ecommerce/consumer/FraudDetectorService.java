package br.com.ecommerce.consumer;

import br.com.ecommerce.common.*;
import br.com.ecommerce.message.Order;
import org.apache.kafka.clients.consumer.ConsumerRecord;

import java.math.BigDecimal;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class FraudDetectorService implements ConsumerFunction<Order> {

    private final KafkaDispatcher<Order> orderKafkaDispatcher = new KafkaDispatcher<>();

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        var fraudDetectorService = new FraudDetectorService();
        try(var service = new KafkaService(FraudDetectorService.class.getSimpleName(),
                "ECOMMERCE_NEW_ORDER", fraudDetectorService::parse,
                Map.of())) {
            service.run();
        }
    }

    @Override
    public void parse(ConsumerRecord<String, Message<Order>> record) throws ExecutionException, InterruptedException {
        Message<Order> message = record.value();

        System.out.println("---------------------------------------------");
        System.out.println("Processing new order, checking for fraud ...");
        System.out.println("Key : " + record.key());
        System.out.println("Value : " + message);
        System.out.println("Partition : " + record.partition());
        System.out.println("Offset : " + record.offset());
        System.out.println("Timestamp : " + record.timestamp());


        var order = message.getPayload();

        String key = order.getEmail();

        CorrelationId correlationId =  message.getId().continueWith(FraudDetectorService.class.getSimpleName());

        if(isFraud(order)){
            //pretending that the fraud happens when the amount is >= 4500
            System.out.println("Order is fraud !!! " + order);
            String topic = "ECOMMERCE_ORDER_REJECTED";
            orderKafkaDispatcher.send(topic, key,correlationId, order);
        }else{
            System.out.println("Order approved " + order);
            String topic = "ECOMMERCE_ORDER_APPROVED";
            orderKafkaDispatcher.send(topic, key,correlationId, order);
        }


    }

    private boolean isFraud(Order order) {
        return order.getAmount().compareTo(new BigDecimal(4500)) >= 0;
    }
}
