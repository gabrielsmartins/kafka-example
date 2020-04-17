package br.com.ecommerce.message;

import java.math.BigDecimal;

public class Order {

    private String userId;
    private String orderId;
    private BigDecimal amount;

    public Order(String userId, String orderId, BigDecimal amount) {
        this.userId = userId;
        this.orderId = orderId;
        this.amount = amount;
    }
}
