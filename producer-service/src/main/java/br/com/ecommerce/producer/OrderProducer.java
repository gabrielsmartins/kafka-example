package br.com.ecommerce.producer;

import br.com.ecommerce.common.CorrelationId;
import br.com.ecommerce.message.Order;

import java.math.BigDecimal;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

public class OrderProducer {

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        try (var orderDispatcher = new KafkaDispatcher<Order>()) {
            String email = Math.random() + "@email.com";
            for (var i = 0; i < 10; i++) {

                var orderId = UUID.randomUUID().toString();
                var amount = new BigDecimal(Math.random() * 5000 + 1);

                var order = new Order(orderId, amount, email);

                String newOrderTopic = "ECOMMERCE_NEW_ORDER";
                CorrelationId correlationId = new CorrelationId(OrderProducer.class.getSimpleName());
                orderDispatcher.send(newOrderTopic, email, correlationId, order);
            }
        }
    }


}
