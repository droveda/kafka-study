package com.droveda;

import com.droveda.model.Email;
import com.droveda.model.Order;

import java.math.BigDecimal;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

public class NewOrderMain {

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        var email = Math.random() + "@email.com";

        try (var orderDispatcher = new KafkaDispatcher<Order>()) {
            var orderId = UUID.randomUUID().toString();
            var amount = new BigDecimal(Math.random() * 5000 + 1);
            var order = new Order(orderId, amount, email);

            orderDispatcher.send("ECOMMERCE_NEW_ORDER", email, order);
        }

        try (var emailDispatcher = new KafkaDispatcher<Email>()) {
            var body = "Thank you for your order! we are processing your order!";
            var emailCode = new Email("Assunto do email", body);

            emailDispatcher.send("ECOMMERCE_SEND_EMAIL", email, emailCode);
        }
    }


}
