package com.example.demo.consumer;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.errors.WakeupException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

public class ConsumerRunnable implements Runnable {

    private final Long latency;
    private String consumerId;
    private KafkaConsumer<String, String> consumer;
    private List<String> messages = Collections.synchronizedList(new ArrayList<>());
    private String topicName;
    private Logger logger = LoggerFactory.getLogger(ConsumerRunnable.class.getName());


    public ConsumerRunnable(KafkaConsumer consumer, String topicName, Long latency, String consumerId) {
        this.consumer = consumer;
        this.topicName = topicName;
        this.latency = latency;
        this.consumerId = consumerId;
    }

    @Override
    public void run() {


        System.out.println("consumer: " + consumer.hashCode());
        consumer.subscribe(Collections.singleton(topicName));
        System.out.println("Subscription ended");

        try {
            while (true) {

                if (Thread.currentThread().isInterrupted()) {
                    throw new InterruptedException();
                }

                synchronized (consumer) {
                    ConsumerRecords<String, String> records =
                            consumer.poll(Duration.ofMillis(100));
                    for (ConsumerRecord<String, String> record : records) {
                        System.out.println("ADDING TO LIST: " + record.value());
                        addMessage(record.value());
                    }
                }

                //for allowing other threads to acquire lock,
                //todo replace with some fair locking mechanism
                Thread.sleep(10);
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

    private void addMessage(String value) {
        this.messages.add(value);

    }

    public List<String> getMessages() {
        return new ArrayList<>(messages);

    }


    public ConsumerData getData() {

        ConsumerData consumerData = new ConsumerData();
        consumerData.setRecords(getMessages());


        synchronized (consumer) {
            Set<TopicPartition> assignment = consumer.assignment();
            for (TopicPartition topicPartition : assignment) {
                int partition = topicPartition.partition();
                consumerData.addPartition(partition);
            }
            consumerData.setConsumerGroup(consumer.groupMetadata().groupId());

            //consumerData.setMemberId(consumer.groupMetadata().memberId());
        }


        consumerData.setLatency(latency);
        consumerData.setConsumerId(consumerId);


        return consumerData;


    }


    /**
     * public class KafkaConsumerRunner implements Runnable {
     *      private final AtomicBoolean closed = new AtomicBoolean(false);
     *      private final KafkaConsumer consumer;
     *
     *      public KafkaConsumerRunner(KafkaConsumer consumer) {
     *        this.consumer = consumer;
     *      }
     *
     *      public void run() {
     *          try {
     *              consumer.subscribe(Arrays.asList("topic"));
     *              while (!closed.get()) {
     *                  ConsumerRecords records = consumer.poll(Duration.ofMillis(10000));
     *                  // Handle new records
     *              }
     *          } catch (WakeupException e) {
     *              // Ignore exception if closing
     *              if (!closed.get()) throw e;
     *          } finally {
     *              consumer.close();
     *          }
     *      }
     *
     *      // Shutdown hook which can be called from a separate thread
     *      public void shutdown() {
     *          closed.set(true);
     *          consumer.wakeup();
     *      }
     *  }
     */
}
