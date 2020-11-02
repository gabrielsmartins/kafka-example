package br.com.ecommerce.consumer;

import br.com.ecommerce.LocalDatabase;
import br.com.ecommerce.common.CorrelationId;
import br.com.ecommerce.common.Message;
import br.com.ecommerce.message.Order;
import br.com.ecommerce.producer.KafkaDispatcher;
import org.apache.kafka.clients.consumer.ConsumerRecord;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;

public class FraudDetectorService implements ConsumerService<Order> {

    private final KafkaDispatcher<Order> orderKafkaDispatcher = new KafkaDispatcher<>();
    private final LocalDatabase database;

    public FraudDetectorService() throws SQLException {
        this.database = new LocalDatabase("users");
        this.database.createTableIfNotExists("create table if not exists frauds (uuid varchar(200), " +
                                                 "created_at datetime, " +
                                                 "is_fraud boolean " +
                                                 "constraint PK_Fraud primary key (uuid,created_at))");
    }

    public static void main(String[] args) throws Exception {
        new ServiceRunner<>(FraudDetectorService::new).start(1);
    }

    @Override
    public String getConsumerGroup() {
        return FraudDetectorService.class.getSimpleName();
    }

    @Override
    public String getTopic() {
        return "ECOMMERCE_NEW_ORDER";
    }

    @Override
    public void parse(ConsumerRecord<String, Message<Order>> record) throws Exception{
        Message<Order> message = record.value();

        System.out.println("---------------------------------------------");
        System.out.println("Processing new order, checking for fraud ...");
        System.out.println("Key : " + record.key());
        System.out.println("Value : " + message);
        System.out.println("Partition : " + record.partition());
        System.out.println("Offset : " + record.offset());
        System.out.println("Timestamp : " + record.timestamp());


        var order = message.getPayload();

        if(wasProcessed(order)){
            return;
        }

        String key = order.getEmail();

        CorrelationId correlationId =  message.getId().continueWith(FraudDetectorService.class.getSimpleName());

        if(isFraud(order)){
            //pretending that the fraud happens when the amount is >= 4500
            this.database.update("insert into frauds (uuid,created_at,is_fraud) values(?,?,true)", order.getOrderId(), LocalDateTime.now());
            System.out.println("Order is fraud !!! " + order);
            String topic = "ECOMMERCE_ORDER_REJECTED";
            orderKafkaDispatcher.send(topic, key,correlationId, order);
        }else{
            this.database.update("insert into frauds (uuid,created_at,is_fraud) values(?,?,false)", order.getOrderId(), LocalDateTime.now());
            System.out.println("Order approved " + order);
            String topic = "ECOMMERCE_ORDER_APPROVED";
            orderKafkaDispatcher.send(topic, key,correlationId, order);
        }


    }

    private boolean wasProcessed(Order order) throws SQLException {
        ResultSet resultSet = this.database.query("select * from frauds where uuid = ? limit 1", order.getOrderId());
        return resultSet.next();
    }

    private boolean isFraud(Order order) {
        return order.getAmount().compareTo(new BigDecimal(4500)) >= 0;
    }
}
