package com.example.demo;


import com.example.demo.consumer.ConsumerThreadPool;
import com.example.demo.dictionary.Dictionary;
import com.example.demo.producer.ProducerRunnable;
import com.example.demo.producer.ProducerThreadPool;
import org.apache.kafka.clients.admin.Admin;
import org.apache.kafka.clients.admin.CreateTopicsResult;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.common.KafkaFuture;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;

import java.util.Collections;
import java.util.concurrent.ExecutionException;


@SpringBootApplication
public class DemoApplication {

    private Logger logger = LoggerFactory.getLogger(DemoApplication.class.getName());


    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }


    @Value(value = "${kafka.topic}")
    private String topicName;

    @Autowired
    private ConsumerThreadPool consumerThreadPool;

    @Autowired
    private ProducerThreadPool producerThreadPool;

    @Autowired
    private Admin admin;
    @Autowired
    private NewTopic newTopic;

    @EventListener(ApplicationReadyEvent.class)
    public void doSomethingAfterStartup() {


        CreateTopicsResult result = admin.createTopics(
                Collections.singleton(newTopic)
        );

        KafkaFuture<Void> future = result.values().get(topicName);
        try {
            future.get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        Dictionary.loadData();
        consumerThreadPool.start();
        producerThreadPool.start();


    }


}
