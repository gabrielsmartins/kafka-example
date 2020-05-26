package br.com.ecommerce.consumer;

import br.com.ecommerce.common.ConsumerFunction;
import br.com.ecommerce.common.KafkaDispatcher;
import br.com.ecommerce.common.KafkaService;
import br.com.ecommerce.message.User;
import org.apache.kafka.clients.consumer.ConsumerRecord;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class BatchMessageService implements ConsumerFunction<String> {

    private final Connection connection;

    private final KafkaDispatcher<User> userDispatcher = new KafkaDispatcher<>();

    public BatchMessageService() throws SQLException {
        String url ="jdbc:sqlite:users_database.db";
        this.connection = DriverManager.getConnection(url);
        PreparedStatement preparedStatement = this.connection.prepareStatement("create table if not exists users(" +
                "uuid varchar(200) primary key, " +
                "email varchar(200))");
        preparedStatement.execute();
    }

    public static void main(String[] args) throws SQLException {
        var batchService = new BatchMessageService();
        try(var service = new KafkaService(CreateUserService.class.getSimpleName(),
                "USER_NOTIFY_ALL_USERS", batchService::parse,
                String.class,
                Map.of())) {
            service.run();
        }
    }

    @Override
    public void parse(ConsumerRecord<String, String> record) throws ExecutionException, InterruptedException, SQLException {
        System.out.println("---------------------------------------------");
        System.out.println("Processing new batch");
        System.out.println("Key : " + record.key());
        System.out.println("Value : " + record.value());
        System.out.println("Partition : " + record.partition());
        System.out.println("Offset : " + record.offset());
        System.out.println("Timestamp : " + record.timestamp());

        for(User user : getAll()){
            userDispatcher.send(record.value(), user.getUuid(), user);
        }
    }

    private List<User> getAll() throws SQLException {
        var preparedStatement = this.connection.prepareStatement("select uuid from users");
        var resultSet = preparedStatement.executeQuery();
        List<User> users = new LinkedList<>();
        while(resultSet.next()){
            users.add(new User(resultSet.getString(1)));
        }
        resultSet.next();
        return users;
    }

}
