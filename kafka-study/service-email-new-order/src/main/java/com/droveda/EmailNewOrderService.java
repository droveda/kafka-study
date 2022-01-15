package com.droveda;

import com.droveda.consumer.ConsumerService;
import com.droveda.consumer.ServiceRunner;
import com.droveda.dispatcher.KafkaDispatcher;
import com.droveda.model.Email;
import com.droveda.model.Order;
import org.apache.kafka.clients.consumer.ConsumerRecord;

import java.util.concurrent.ExecutionException;

public class EmailNewOrderService implements ConsumerService<Order> {

    private final KafkaDispatcher<Email> emailDispatcher = new KafkaDispatcher<>();

    public static void main(String[] args) {
        new ServiceRunner<>(EmailNewOrderService::new).start(1);
    }

    @Override
    public void parse(ConsumerRecord<String, Message<Order>> record) throws ExecutionException, InterruptedException {
        System.out.println("-----------");
        System.out.println("Processing new order, preparing email");
        System.out.println(record.value());
        var order = record.value().getPayload();


        var body = "Thank you for your order! we are processing your order!";
        var email = new Email("Assunto do email", body);

        emailDispatcher.send("ECOMMERCE_SEND_EMAIL", order.getEmail(), email,
                record.value().getId().continueWith(EmailNewOrderService.class.getSimpleName()));

    }

    @Override
    public String getTopic() {
        return "ECOMMERCE_NEW_ORDER";
    }

    @Override
    public String getConsumerGroup() {
        return EmailNewOrderService.class.getSimpleName();
    }
}
