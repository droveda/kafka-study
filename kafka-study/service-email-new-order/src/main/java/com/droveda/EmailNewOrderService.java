package com.droveda;

import com.droveda.consumer.KafkaService;
import com.droveda.dispatcher.KafkaDispatcher;
import com.droveda.model.Email;
import com.droveda.model.Order;
import org.apache.kafka.clients.consumer.ConsumerRecord;

import java.util.HashMap;
import java.util.concurrent.ExecutionException;

public class EmailNewOrderService {

    private final KafkaDispatcher<Email> emailDispatcher = new KafkaDispatcher<>();

    public static void main(String[] args) {
        var emailService = new EmailNewOrderService();
        try (var service = new KafkaService<>(EmailNewOrderService.class.getSimpleName(),
                "ECOMMERCE_NEW_ORDER",
                emailService::parse,
                new HashMap<>())) {
            service.run();
        }
    }

    private void parse(ConsumerRecord<String, Message<Order>> record) throws ExecutionException, InterruptedException {
        System.out.println("-----------");
        System.out.println("Processing new order, preparing email");
        System.out.println(record.value());
        var order = record.value().getPayload();


        var body = "Thank you for your order! we are processing your order!";
        var email = new Email("Assunto do email", body);

        emailDispatcher.send("ECOMMERCE_SEND_EMAIL", order.getEmail(), email,
                record.value().getId().continueWith(EmailNewOrderService.class.getSimpleName()));

    }


}
