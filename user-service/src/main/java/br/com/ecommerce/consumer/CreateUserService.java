package br.com.ecommerce.consumer;

import br.com.ecommerce.LocalDatabase;
import br.com.ecommerce.common.Message;
import br.com.ecommerce.message.Order;
import org.apache.kafka.clients.consumer.ConsumerRecord;

import java.sql.SQLException;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

public class CreateUserService implements ConsumerService<Order> {

    private final LocalDatabase database;

    public CreateUserService() throws SQLException {
        this.database = new LocalDatabase("users");
        this.database.createTableIfNotExists("create table if not exists users (uuid varchar(200), email varchar(200), constraint PK_User primary key (uuid))");
    }

    public static void main(String[] args) throws SQLException, ExecutionException, InterruptedException {
        new ServiceRunner<>(CreateUserService::new).start(1);
    }

    @Override
    public String getConsumerGroup() {
        return CreateUserService.class.getSimpleName();
    }

    @Override
    public String getTopic() {
        return "ECOMMERCE_NEW_ORDER";
    }

    @Override
    public void parse(ConsumerRecord<String, Message<Order>> record) throws Exception {
        System.out.println("---------------------------------------------");
        System.out.println("Processing new order, checking for new user");
        System.out.println("Key : " + record.key());
        System.out.println("Value : " + record.value());
        System.out.println("Partition : " + record.partition());
        System.out.println("Offset : " + record.offset());
        System.out.println("Timestamp : " + record.timestamp());


        Message<Order> message = record.value();
        Order order = message.getPayload();
        String email = order.getEmail();

        if (isNewUser(email)) {
            insertNewUser(email);
        }
    }

    private void insertNewUser(String email) throws SQLException {
        String id = UUID.randomUUID().toString();
        this.database.update("insert into users (uuid,email) values (?,?)", id, email);
        System.out.println("Usuário adicionado com sucesso");
    }

    private boolean isNewUser(String email) throws SQLException {
        var resultSet = this.database.query("select uuid from users where email = ? limit 1", email);
        return !resultSet.next();
    }
}
