package com.example.demo.producer;

import com.example.demo.dictionary.Dictionary;
import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;

import java.util.List;

public class ProducerRunnable implements Runnable {

    private KafkaProducer<String, String> producer;
    private String topicName;
    private volatile Long latency;
    private List<String> messages;

    private Logger logger = LoggerFactory.getLogger(ProducerRunnable.class.getName());

    public ProducerRunnable(KafkaProducer<String, String> producer, String topicName, Long latency, List<String> messages) {
        this.producer = producer;
        this.topicName = topicName;
        this.latency = latency;
        this.messages = messages;
    }

    @Override
    public void run() {

        while (true) {
            String randomWord = Dictionary.getRandomWord();

            ProducerRecord<String, String> producerRecord =
                    new ProducerRecord<>(topicName, randomWord);

            producer.send(producerRecord, (metadata, e) -> {
                if (e == null) {
                    logger.info("Received new metadata. \n" +
                            "Topic: " + metadata.topic() + "\n" +
                            "Partition: " + metadata.partition() + "\n" +
                            "Offset: " + metadata.offset() + "\n" +
                            "Timestamp: " + metadata.timestamp());
                    addMessage(randomWord);
                } else {
                    logger.error("Error while producing", e);
                }
            });
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }

    private void addMessage(String randomWord) {
        messages.add(randomWord);
    }

    public void changeLatency(Long newLatency) {
        this.latency = newLatency;
    }
}


