package com.example.demo.consumer;

import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
	private Deque<ConsumerRunnableReference> consumerRunnables = new LinkedList<>();
	private BeanFactory beanFactory;

	public ConsumerThreadPool(BeanFactory beanFactory) {
		this.beanFactory = beanFactory;
	}

	public synchronized void start(){
		createConsumer();
		createConsumer();
	}

	private synchronized void createConsumer() {
		ConsumerRunnableReference consumerRunnable = createConsumerRunnable();
		consumerRunnables.addFirst(consumerRunnable);
		runningThreads++;
	}


	private ConsumerRunnableReference createConsumerRunnable() {
		ConsumerRunnable consumerRunnable = new ConsumerRunnable(beanFactory.getBean(KafkaConsumer.class), topicName, latency);
		Future future = threadPool.submit(consumerRunnable);
		ConsumerRunnableReference task = new ConsumerRunnableReference(future, consumerRunnable);
		return task;
	}


	public synchronized boolean addConsumer(){
		if (runningThreads == MAX_CONSUMERS){
			logger.info("Max number of Consumers reached");
			return false;
		}else {
			createConsumer();
			logger.info("Consumer created succesfully");
			return true;
		}
	}


	public synchronized boolean removeConsumer(){
		ConsumerRunnableReference consumer = consumerRunnables.poll();
		if (consumer == null){
			logger.info("No tasks for cancellation");
			return false;
		}
		consumer.getTask().cancel(true);
		runningThreads--;
		return true;
	}


	public synchronized List<List<String>> getMessages(){
		return consumerRunnables.stream().map(task -> task.getMessages()).collect(Collectors.toList());

	}

	public synchronized String log() {
		return String.format("Tasks size: %s, thread number: %s ", consumerRunnables.size(), runningThreads) ;
	}


	public List<ConsumerData> getConsumerData() {
		return consumerRunnables.stream().map(task -> task.getConsumerData()).collect(Collectors.toList());
	}
}
