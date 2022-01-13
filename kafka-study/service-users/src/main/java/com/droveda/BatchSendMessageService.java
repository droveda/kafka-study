package com.droveda;

import com.droveda.model.User;
import org.apache.kafka.clients.consumer.ConsumerRecord;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class BatchSendMessageService {

    private final Connection connection;

    private final KafkaDispatcher<User> dispatcher = new KafkaDispatcher<>();

    public BatchSendMessageService() throws SQLException {
        String url = "jdbc:sqlite:users_database.db";
        this.connection = DriverManager.getConnection(url);
        try {
            connection.createStatement().execute("create table Users (uuid varchar(200) primary key, email varchar(200))");
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public static void main(String[] args) throws SQLException {
        var batchService = new BatchSendMessageService();
        try (var service = new KafkaService<>(BatchSendMessageService.class.getSimpleName(),
                "SEND_MESSAGE_ALL_USERS",
                batchService::parse,
                String.class,
                new HashMap<>())) {
            service.run();
        }
    }

    private void parse(ConsumerRecord<String, Message<String>> record) throws ExecutionException, InterruptedException, SQLException {
        Message<String> message = record.value();

        System.out.println("-----------");
        System.out.println("Processing new batch");
        System.out.println("Topic:" + message.getPayload());

        var topic = message.getPayload();

        for (User user : getUsers()) {
            dispatcher.send(topic, user.getUuid(), user);
        }

    }

    private List<User> getUsers() throws SQLException {
        var results = connection.prepareStatement("select uuid from Users").executeQuery();

        List<User> users = new ArrayList<>();
        while (results.next()) {
            users.add(new User(results.getString(1)));
        }

        return users;
    }


}
