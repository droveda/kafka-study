package com.droveda.consumer;

import com.droveda.CorrelationId;
import com.droveda.Message;
import com.droveda.dispatcher.KafkaDispatcher;
import com.droveda.dispatcher.GsonSerializer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;

import java.io.Closeable;
import java.time.Duration;
import java.util.Collections;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;
import java.util.regex.Pattern;

public class KafkaService<T> implements Closeable {

    private final String groupId;
    private final KafkaConsumer<String, Message<T>> consumer;
    private final ConsumerFunction parse;

    public KafkaService(String groupId, String topic, ConsumerFunction<T> parse, Map<String, String> map) {
        this.groupId = groupId;
        this.consumer = new KafkaConsumer<>(properties(map));
        this.parse = parse;
        consumer.subscribe(Collections.singletonList(topic));
    }

    public KafkaService(String groupId, Pattern topic, ConsumerFunction<T> parse, Map<String, String> map) {
        this.groupId = groupId;
        this.consumer = new KafkaConsumer<>(properties(map));
        this.parse = parse;
        consumer.subscribe(topic);
    }

    private Properties properties(Map<String, String> overrideProperties) {
        var properties = new Properties();
        properties.setProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "127.0.0.1:29092");
        properties.setProperty(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        properties.setProperty(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, GsonDeserializer.class.getName());
        properties.setProperty(ConsumerConfig.GROUP_ID_CONFIG, this.groupId);

        //apenas para dar um nome para o consumer
        properties.setProperty(ConsumerConfig.CLIENT_ID_CONFIG, UUID.randomUUID().toString());

        //poll de 1 em 1. Faz commit de 1 em 1
        properties.setProperty(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, "1");

//        properties.setProperty(GsonDeserializer.TYPE_CONFIG, type.getName());

        properties.setProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest");

        properties.putAll(overrideProperties);

        return properties;
    }

    public void run() {

        try (var dqlDispatcher = new KafkaDispatcher<>()) {
            while (true) {
                var records = consumer.poll(Duration.ofMillis(100));
                if (!records.isEmpty()) {
                    System.out.println("Encontrei " + records.count() + " registros!");

                    for (var record : records) {
                        try {
                            this.parse.consume(record);
                        } catch (Exception e) {
                            e.printStackTrace();
                            var message = record.value();
                            try {
                                dqlDispatcher.send("ECOMMERCE_DLQ", message.getId().toString(),
                                        new GsonSerializer<>().serialize("", message),
                                        message.getId().continueWith(new CorrelationId("DeadLetter")));
                            } catch (Exception ex) {
                                ex.printStackTrace();
                            }
                        }
                    }
                }
            }
        }
    }

    @Override
    public void close() {
        consumer.close();
    }
}
