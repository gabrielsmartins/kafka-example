package br.com.ecommerce.consumer;

import br.com.ecommerce.common.Message;
import org.apache.kafka.clients.consumer.ConsumerRecord;

public class EmailService implements ConsumerService<String> {

    public static void main(String[] args) throws Exception {
       new ServiceRunner<>(EmailService::new).start(5);
    }

    @Override
    public void parse(ConsumerRecord<String, Message<String>> record) throws Exception {
        System.out.println("---------------------------------------------");
        System.out.println("Processing new order, checking for fraud ...");
        System.out.println("Key : " + record.key());
        System.out.println("Value : " + record.value());
        System.out.println("Partition : " + record.partition());
        System.out.println("Offset : " + record.offset());
        System.out.println("Timestamp : " + record.timestamp());
        System.out.println("Email sent");
    }

    @Override
    public String getConsumerGroup() {
        return EmailService.class.getSimpleName();
    }

    @Override
    public String getTopic() {
        return "ECOMMERCE_SEND_EMAIL";
    }
}
