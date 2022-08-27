package com.example.demo.consumer;

import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

@Component
public class ConsumerThreadPool {

    private Logger logger = LoggerFactory.getLogger(ConsumerThreadPool.class.getName());
    private Integer runningThreads = 0;
    private Long latency = 1000L;
    @Value(value = "${kafka.topic}")
    private String topicName;
    private static final Integer MAX_CONSUMERS = 5;
    private static ExecutorService threadPool = Executors.newFixedThreadPool(MAX_CONSUMERS);
    private Map<String, ConsumerRunnableReference> consumerRunnables = new HashMap<>();
    private BeanFactory beanFactory;


    public ConsumerThreadPool(BeanFactory beanFactory) {
        this.beanFactory = beanFactory;
    }

    public synchronized void start() {
        createConsumer("one");
        createConsumer("two");
    }

    private synchronized void createConsumer(String consumerId) {
        ConsumerRunnableReference consumerRunnable = createConsumerRunnable(consumerId);
        consumerRunnables.put(consumerId, consumerRunnable);
        runningThreads++;
    }


    private ConsumerRunnableReference createConsumerRunnable(String consumerId) {
        ConsumerRunnable consumerRunnable = new ConsumerRunnable(beanFactory.getBean(KafkaConsumer.class), topicName, latency, consumerId);
        Future future = threadPool.submit(consumerRunnable);
        ConsumerRunnableReference task = new ConsumerRunnableReference(future, consumerRunnable);
        return task;
    }


    public synchronized boolean addConsumer(String id) {
        if (runningThreads == MAX_CONSUMERS || consumerRunnables.containsKey(id)) {
            logger.warn("Max number of Consumers reached or consumer already exists");
            return false;
        } else {
            createConsumer(id);
            logger.info("Consumer created succesfully");
            return true;
        }
    }


    public synchronized boolean removeConsumer(String consumerId) {
        ConsumerRunnableReference consumer = consumerRunnables.get(consumerId);
        if (consumer == null) {
            logger.info("No tasks for cancellation");
            return false;
        }
        consumer.getTask().cancel(true);
        consumerRunnables.remove(consumerId);
        runningThreads--;
        return true;
    }


    public synchronized List<ConsumerData> getConsumerData() {
        return consumerRunnables.values().stream().map(task -> task.getConsumerData()).collect(Collectors.toList());
    }

    public synchronized void updateConsumer(String id, long latency) {
        consumerRunnables.get(id).update(latency);
    }
}
