package com.droveda;

import com.droveda.consumer.ConsumerService;
import com.droveda.consumer.ServiceRunner;
import com.droveda.model.Email;
import org.apache.kafka.clients.consumer.ConsumerRecord;

public class EmailService implements ConsumerService<Email> {

    public static void main(String[] args) throws Exception {
        new ServiceRunner(EmailService::new).start(5);
    }

    @Override
    public String getConsumerGroup() {
        return EmailService.class.getSimpleName();
    }

    @Override
    public String getTopic() {
        return "ECOMMERCE_SEND_EMAIL";
    }

    @Override
    public void parse(ConsumerRecord<String, Message<Email>> record) {
        System.out.println("-----------");
        System.out.println("Sending email");
        System.out.println(record.key());
        System.out.println(record.value());
        System.out.println(record.partition());
        System.out.println(record.offset());
        try {
            Thread.sleep(5000);
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
        System.out.println("Email sent!");
    }


}
