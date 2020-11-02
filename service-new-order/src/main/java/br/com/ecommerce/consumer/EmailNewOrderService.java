package br.com.ecommerce.consumer;

import br.com.ecommerce.common.Message;
import br.com.ecommerce.message.Order;
import br.com.ecommerce.producer.KafkaDispatcher;
import org.apache.kafka.clients.consumer.ConsumerRecord;

import java.util.concurrent.ExecutionException;

public class EmailNewOrderService implements ConsumerService<Order> {

    private final KafkaDispatcher<String> emailDispatcher = new KafkaDispatcher<>();

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        new ServiceRunner<>(EmailNewOrderService::new).start(5);
    }

    @Override
    public String getConsumerGroup() {
        return EmailNewOrderService.class.getSimpleName();
    }

    @Override
    public String getTopic() {
        return "ECOMMERCE_NEW_ORDER";
    }

    @Override
    public void parse(ConsumerRecord<String, Message<Order>> record) throws Exception {
        var message = record.value();

        System.out.println("---------------------------------------------");
        System.out.println("Processing new order, preparing e-mail ...");
        System.out.println("Key : " + record.key());
        System.out.println("Value : " + message);
        System.out.println("Partition : " + record.partition());
        System.out.println("Offset : " + record.offset());
        System.out.println("Timestamp : " + record.timestamp());

        var order = message.getPayload();
        var emailMessage = "Thank you for your order! We are processing your order!";
        var topic = "ECOMMERCE_SEND_EMAIL";
        var correlationId = message.getId().continueWith(EmailNewOrderService.class.getSimpleName());
        emailDispatcher.send(topic, order.getEmail(), correlationId, emailMessage);
    }

}
