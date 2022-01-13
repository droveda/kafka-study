package com.droveda;

import com.droveda.model.Order;
import org.apache.kafka.clients.consumer.ConsumerRecord;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;

public class FraudDetectorService {

    private final KafkaDispatcher<Order> dispatcher = new KafkaDispatcher<>();

    public static void main(String[] args) {
        var fraudService = new FraudDetectorService();
        try (var service = new KafkaService<>(FraudDetectorService.class.getSimpleName(),
                "ECOMMERCE_NEW_ORDER",
                fraudService::parse,
                new HashMap<>())) {
            service.run();
        }
    }

    private void parse(ConsumerRecord<String, Message<Order>> record) throws ExecutionException, InterruptedException {
        System.out.println("-----------");
        System.out.println("Processing new order, checking for fraud");
        System.out.println(record.key());
        System.out.println(record.value());
        System.out.println(record.partition());
        System.out.println(record.offset());
        try {
            Thread.sleep(5000);
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }

        var order = record.value().getPayload();
        if (isFraud(order)) {
            //pretending fraud will happen when the amount is >= 4500
            System.out.println("Order is a fraud!!!");
            dispatcher.send("ECOMMERCE_ORDER_REJECTED", order.getEmail(), order,
                    record.value().getId().continueWith(new CorrelationId(FraudDetectorService.class.getSimpleName())));
        } else {
            System.out.println("Approved: " + order);
            dispatcher.send("ECOMMERCE_ORDER_APPROVED", order.getEmail(), order,
                    record.value().getId().continueWith(new CorrelationId(FraudDetectorService.class.getSimpleName())));
        }


        System.out.println("Order processed!");
    }

    private boolean isFraud(Order order) {
        return order.getAmount().compareTo(new BigDecimal("4500")) >= 0;
    }

}
