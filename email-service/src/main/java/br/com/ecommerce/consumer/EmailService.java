package br.com.ecommerce.consumer;

import br.com.ecommerce.common.ConsumerFunction;
import br.com.ecommerce.common.KafkaService;
import br.com.ecommerce.common.Message;
import org.apache.kafka.clients.consumer.ConsumerRecord;

import java.util.Map;

public class EmailService implements ConsumerFunction<String> {

    public static void main(String[] args) {
        var emailService = new EmailService();
        try (var service = new KafkaService(EmailService.class.getSimpleName(),
                "ECOMMERCE_SEND_EMAIL",
                emailService::parse,
                String.class,
                Map.of())){
            service.run();
        }
    }

    @Override
    public void parse(ConsumerRecord<String, Message<String>> record) {
        System.out.println("---------------------------------------------");
        System.out.println("Processing new order, checking for fraud ...");
        System.out.println("Key : " + record.key());
        System.out.println("Value : " + record.value());
        System.out.println("Partition : " + record.partition());
        System.out.println("Offset : " + record.offset());
        System.out.println("Timestamp : " + record.timestamp());
        System.out.println("Email sent");
    }


}
