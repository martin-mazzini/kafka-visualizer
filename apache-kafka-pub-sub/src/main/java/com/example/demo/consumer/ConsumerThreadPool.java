package com.example.demo.consumer;

import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
	private Deque<ConsumerRunnableReference> tasks = new LinkedList<>();
	private KafkaConsumer<String, String> consumer;

	public ConsumerThreadPool(KafkaConsumer<String, String> consumer) {
		this.consumer = consumer;
	}


	public synchronized void start(){
		createConsumer();
		createConsumer();
	}

	private void createConsumer() {
		ConsumerRunnableReference task = createConsumerRunnable();
		tasks.addFirst(task);
		runningThreads++;
	}


	private ConsumerRunnableReference createConsumerRunnable() {
		List<String> messages = new ArrayList<>();
		ConsumerRunnable consumerRunnable = new ConsumerRunnable(messages, consumer, topicName, latency);
		Future<?> future = threadPool.submit(consumerRunnable);
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
			tasks.add(task);
			logger.info("ConsumerRunnable submit correcto");
			return true;
		}
	}
	public synchronized boolean removeConsumer(){
		ConsumerRunnableReference poll = tasks.poll();
		if (poll == null){
			logger.info("No quedan mas consumers para parar");
			return false;
		}
		poll.task.cancel(true);
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
			Thread.currentThread().interrupt();
		}
		if (poll.task.isCancelled()){
			logger.info("ConsumerRunnable cancelado correctamente");
			runningThreads = runningThreads - 1;
			return true;
		}else {
			return false;
		}

	}


	public synchronized List<List<String>> getMessages(){
		return tasks.stream().map(task -> task.messages).collect(Collectors.toList());

	}

	public synchronized String log() {
		return String.format("Tasks size: %s, thread number: %s ", tasks.size(), runningThreads );
	}





}
