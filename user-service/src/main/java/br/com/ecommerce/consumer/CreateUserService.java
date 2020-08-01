package br.com.ecommerce.consumer;

import br.com.ecommerce.common.ConsumerFunction;
import br.com.ecommerce.common.KafkaService;
import br.com.ecommerce.common.Message;
import br.com.ecommerce.message.Order;
import org.apache.kafka.clients.consumer.ConsumerRecord;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

public class CreateUserService implements ConsumerFunction<Order> {

    private final Connection connection;

    public CreateUserService() throws SQLException {
        String url ="jdbc:sqlite:users_database.db";
        this.connection = DriverManager.getConnection(url);
        PreparedStatement preparedStatement = this.connection.prepareStatement("create table if not exists users (uuid varchar(200), email varchar(200), constraint PK_User primary key (uuid))");
        preparedStatement.execute();
    }

    public static void main(String[] args) throws SQLException {
        var createUserService = new CreateUserService();
        try(var service = new KafkaService(CreateUserService.class.getSimpleName(),
                "ECOMMERCE_NEW_ORDER", createUserService::parse,
                Order.class,
                Map.of())) {
            service.run();
        }
    }

    @Override
    public void parse(ConsumerRecord<String, Message<Order>> record) throws ExecutionException, InterruptedException, SQLException {
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

        if(isNewUser(email)){
            insertNewUser(email);
        }
    }

    private void insertNewUser(String email) throws SQLException {
            var preparedStatement = this.connection.prepareStatement("insert into users (uuid,email) values (?,?)");
            preparedStatement.setString(1, UUID.randomUUID().toString());
            preparedStatement.setString(2, email);
            preparedStatement.execute();
            System.out.println("Usuário adicionado com sucesso");

    }

    private boolean isNewUser(String email) throws SQLException {
        var preparedStatement = this.connection.prepareStatement("select uuid from users where email = ? limit 1");
        preparedStatement.setString(1, email);
        var resultSet = preparedStatement.executeQuery();
        return !resultSet.next();
    }
}
