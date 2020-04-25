package br.com.ecommerce.producer;

import br.com.ecommerce.common.KafkaDispatcher;
import br.com.ecommerce.message.Order;

import java.math.BigDecimal;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

public class OrderProducer {

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        try (var orderDispatcher = new KafkaDispatcher<Order>()) {
            try (var emailDispatcher = new KafkaDispatcher<String>()) {
                String email = Math.random() + "@email.com";
                for (var i = 0; i < 10; i++) {

                    var orderId = UUID.randomUUID().toString();
                    var amount = new BigDecimal(Math.random() * 5000 + 1);

                    var order = new Order(orderId, amount, email);

                    var key = UUID.randomUUID().toString();
                    var value = "150000,244550.50";
                    orderDispatcher.send("ECOMMERCE_NEW_ORDER", email, order);

                    var emailMessage = "Thank you for your order! We are processing your order!";
                    emailDispatcher.send("ECOMMERCE_SEND_EMAIL", email, emailMessage);
                }
            }
        }
    }


}
