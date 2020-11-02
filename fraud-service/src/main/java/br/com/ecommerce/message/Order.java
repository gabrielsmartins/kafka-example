package br.com.ecommerce.message;

import java.math.BigDecimal;

public class Order {

    private String orderId;
    private BigDecimal amount;
    private String email;

    public Order(String orderId, BigDecimal amount, String email) {
        this.orderId = orderId;
        this.amount = amount;
        this.email = email;
    }

    public String getOrderId() {
        return this.orderId;
    }

    public BigDecimal getAmount() {
        return this.amount;
    }

    @Override
    public String toString() {
        return "Order{" +
                "orderId='" + this.orderId + '\'' +
                ", amount=" + this.amount +
                ", email='" + this.email + '\'' +
                '}';
    }

    public String getEmail() {
        return this.email;
    }
}
