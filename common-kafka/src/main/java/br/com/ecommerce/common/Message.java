package br.com.ecommerce.common;

public class Message<T> {

    private final CorrelationId id;
    private final T payload;

    public Message(CorrelationId id, T payload) {
        this.id = id;
        this.payload = payload;
    }

    @Override
    public String toString() {
        return "Message{" +
                "correlationId=" + id +
                ", payload=" + payload +
                '}';
    }

    public CorrelationId getId() {
        return id;
    }

    public T getPayload() {
        return payload;
    }
}
