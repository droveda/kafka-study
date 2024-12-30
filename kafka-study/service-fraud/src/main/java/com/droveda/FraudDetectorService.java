package com.droveda;

import com.droveda.consumer.ConsumerService;
import com.droveda.consumer.ServiceFactory;
import com.droveda.consumer.ServiceRunner;
import com.droveda.dispatcher.KafkaDispatcher;
import com.droveda.model.Order;
import org.apache.kafka.clients.consumer.ConsumerRecord;

import java.math.BigDecimal;
import java.sql.SQLException;

public class FraudDetectorService implements ConsumerService<Order> {

    private final KafkaDispatcher<Order> dispatcher = new KafkaDispatcher<>();

    private final LocalDatabase localDatabase;

    public FraudDetectorService() throws SQLException {
        this.localDatabase = new LocalDatabase("frauds_database");
        this.localDatabase.createIfNotExists("create table Orders (uuid varchar(200) primary key, is_fraud boolean)");
    }

    public static void main(String[] args) {
        new ServiceRunner<>(new ServiceFactory<Order>() {
            @Override
            public ConsumerService<Order> create() throws Exception {
                return new FraudDetectorService();
            }
        }).start(1);
    }

    @Override
    public void parse(ConsumerRecord<String, Message<Order>> record) throws Exception {
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
        if (wasProcessed(order)) {
            System.out.println("Order was already processed. OrderId: " + order.getOrderId());
            return;
        }

        if (isFraud(order)) {

            localDatabase.update("insert into Orders (uuid, is_fraud) values (?, true)", order.getOrderId());

            //pretending fraud will happen when the amount is >= 4500
            System.out.println("Order is a fraud!!!");
            dispatcher.send("ECOMMERCE_ORDER_REJECTED", order.getEmail(), order,
                    record.value().getId().continueWith(new CorrelationId(FraudDetectorService.class.getSimpleName())));
        } else {

            localDatabase.update("insert into Orders (uuid, is_fraud) values (?, false)", order.getOrderId());

            System.out.println("Approved: " + order);
            dispatcher.send("ECOMMERCE_ORDER_APPROVED", order.getEmail(), order,
                    record.value().getId().continueWith(new CorrelationId(FraudDetectorService.class.getSimpleName())));
        }


        System.out.println("Order processed!");
    }

    private boolean wasProcessed(Order order) throws SQLException {
        var results = localDatabase.query("select uuid from Orders where uuid = ? limit 1", order.getOrderId());
        return results.next();
    }

    private boolean isFraud(Order order) {
        return order.getAmount().compareTo(new BigDecimal("4500")) >= 0;
    }

    @Override
    public String getTopic() {
        return "ECOMMERCE_NEW_ORDER";
    }

    @Override
    public String getConsumerGroup() {
        return FraudDetectorService.class.getSimpleName();
    }
}
