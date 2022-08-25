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

	private void createConsumer() {
		ConsumerRunnableReference consumerRunnable = createConsumerRunnable();
		consumerRunnables.addFirst(consumerRunnable);
		runningThreads++;
	}


	private ConsumerRunnableReference createConsumerRunnable() {
		List<String> messages = new ArrayList<>();
		ConsumerRunnable consumerRunnable = new ConsumerRunnable(messages, beanFactory.getBean(KafkaConsumer.class), topicName, latency);
		Future future = threadPool.submit(consumerRunnable);
		ConsumerRunnableReference task = new ConsumerRunnableReference(messages, future, consumerRunnable);
		return task;
	}


	public synchronized boolean addConsumer(){
		if (runningThreads == MAX_CONSUMERS){
			logger.info("Maximo nro de consumers alcanzado");
			return false;
		}else {
			ConsumerRunnableReference task = createConsumerRunnable();
			runningThreads = runningThreads + 1;
			consumerRunnables.add(task);
			logger.info("ConsumerRunnable submit correcto");
			return true;
		}
	}
	public synchronized boolean removeConsumer(){
		ConsumerRunnableReference consumer = consumerRunnables.poll();
		if (consumer == null){
			logger.info("No quedan mas consumers para parar");
			return false;
		}
		consumer.getTask().cancel(true);
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
			Thread.currentThread().interrupt();
		}
		if (consumer.getTask().isCancelled()){
			logger.info("ConsumerRunnable cancelado correctamente");
			runningThreads = runningThreads - 1;
			return true;
		}else {
			return false;
		}

	}


	public synchronized List<List<String>> getMessages(){
		return consumerRunnables.stream().map(task -> task.getMessages()).collect(Collectors.toList());

	}

	public synchronized String log() {
		return String.format("Tasks size: %s, thread number: %s ", consumerRunnables.size(), runningThreads );
	}





}
