package com.example.demo.consumer;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.errors.WakeupException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.Set;

public class ConsumerRunnable implements Runnable {

    private final Long latency;
    private KafkaConsumer<String, String> consumer;
    private List<String> messages;
    private String topicName;
    private Logger logger = LoggerFactory.getLogger(ConsumerRunnable.class.getName());

    public ConsumerRunnable(List<String> messages, KafkaConsumer consumer, String topicName, Long latency) {
        this.consumer = consumer;
        this.messages = messages;
        this.topicName = topicName;
        this.latency = latency;

    }

    @Override
    public void run() {

        System.out.println("consumer: " + consumer.hashCode());
        consumer.subscribe(Collections.singleton(topicName));


        try {
            while (true) {

                if (Thread.currentThread().isInterrupted()) {
                    throw new InterruptedException();
                }

                ConsumerRecords<String, String> records =
                        consumer.poll(Duration.ofMillis(latency));

                for (ConsumerRecord<String, String> record : records) {
                    addMessage(record.value());
                }

            }
        } catch (WakeupException e) {
            logger.info("Received shutdown signal!");
        } catch (InterruptedException interruptedException) {
            logger.info("Removing consumer");
        } finally {
            try {
                consumer.close();
            } catch (Exception ex) {
                logger.error("Unexpected error in Consumer", ex);
            }

        }

    }

    private synchronized void addMessage(String value) {
        this.messages.add(value);
    }

}
