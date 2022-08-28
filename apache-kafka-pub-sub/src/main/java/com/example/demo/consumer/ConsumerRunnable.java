package com.example.demo.consumer;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.errors.InterruptException;
import org.apache.kafka.common.errors.WakeupException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.*;

public class ConsumerRunnable implements Runnable {

    private volatile long latency;
    private String consumerId;
    private KafkaConsumer<String, String> consumer;
    private List<RecordDTO> messages = Collections.synchronizedList(new ArrayList<>());
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

        synchronized (consumer) {
            consumer.subscribe(Collections.singleton(topicName));
        }
        try {
            while (true) {
                if (Thread.currentThread().isInterrupted()) {
                    
                    throw new InterruptedException();
                }
                synchronized (consumer) {
                    ConsumerRecords<String, String> records =
                            consumer.poll(Duration.ofMillis(10));
                    for (ConsumerRecord<String, String> record : records) {
                        // 
                        addMessage(record);
                    }
                }
                Thread.sleep(latency);
            }
        } catch (WakeupException e) {
            logger.info("Received shutdown signal!");
        } catch (InterruptedException interruptedException) {
            logger.info("Removing consumer, interrupted exceptio");
        } catch(InterruptException e){
            logger.info("Removing consumer, kafka interrupt exception");
        }  finally {
            try {
                logger.info("Finally clause, Removing consumer");
                consumer.close();
            } catch (Exception ex) {
                logger.info("Exception closing consumer");
            }
        }


        


    }

    private void addMessage(ConsumerRecord<String, String> consumerRecord) {
        this.messages.add(new RecordDTO(consumerRecord.value(), consumerRecord.offset()));

    }

    public List<RecordDTO> getMessages() {
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
        }


        consumerData.setLatency(latency);
        consumerData.setConsumerId(consumerId);
        return consumerData;

    }

    public void update(long latency) {
        this.latency = latency;
    }


    //TODO use this
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
