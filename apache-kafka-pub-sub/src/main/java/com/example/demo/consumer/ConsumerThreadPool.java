package com.example.demo.consumer;

import com.example.demo.producer.ProducerThreadPool;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

public class ConsumerThreadPool {



	private Integer runningThreads = 0;
	private Long latency;
	private String topicName;
	private static final Integer MAX_N_THREADS = 5;
	private static ExecutorService threadPool = Executors.newFixedThreadPool(MAX_N_THREADS);
	private Deque<TaskAndMessages> tasks = new LinkedList<>();
	private static ConsumerThreadPool consumerThreadPool;

	private Logger logger = LoggerFactory.getLogger(ProducerThreadPool.class.getName());


	public static ConsumerThreadPool initialize(String topicName, Long latency) {
		consumerThreadPool = new ConsumerThreadPool();
		consumerThreadPool.topicName = topicName;
		consumerThreadPool.latency = latency;
		return consumerThreadPool;
	}

	public synchronized void start(){
		TaskAndMessages task1 = createConsumerRunnable();
		tasks.addFirst(task1);
		runningThreads = runningThreads + 1;
		TaskAndMessages task2 = createConsumerRunnable();
		tasks.addFirst(task2);
		runningThreads = runningThreads + 1;
	}

	public static ConsumerThreadPool getInstance() {
		return consumerThreadPool;
	}

	private TaskAndMessages createConsumerRunnable() {
		List<String> messages = new ArrayList<>();
		ConsumerRunnable consumerRunnable = new ConsumerRunnable(messages, getConsumer(), topicName, latency);
		Future<?> future = threadPool.submit(consumerRunnable);
		TaskAndMessages task = new TaskAndMessages(messages, future, consumerRunnable);
		return task;
	}


	public synchronized boolean addConsumer(){
		if (runningThreads == MAX_N_THREADS){
			logger.info("Maximo nro de consumers alcanzado");
			return false;
		}else {
			TaskAndMessages task = createConsumerRunnable();
			runningThreads = runningThreads + 1;
			tasks.add(task);
			logger.info("ConsumerRunnable submit correcto");
			return true;
		}
	}
	public synchronized boolean removeConsumer(){
		TaskAndMessages poll = tasks.poll();
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




	public static KafkaConsumer<String, String> getConsumer(){
		KafkaConsumer<String, String> consumer;
		Properties properties = new Properties();
		String bootstrapServers = "127.0.0.1:9092";
		String groupId = "test";
		properties.setProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
		properties.setProperty(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
		properties.setProperty(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
		properties.setProperty(ConsumerConfig.GROUP_ID_CONFIG, groupId);
		consumer = new KafkaConsumer<String, String>(properties);
		return consumer;
	}



	private class TaskAndMessages {
		List<String> messages;
		Future task;
		private ConsumerRunnable consumerRunnable;

		public TaskAndMessages(List<String> messages, Future task, ConsumerRunnable consumerRunnable) {
			this.messages = messages;
			this.task = task;
			this.consumerRunnable = consumerRunnable;
		}

	
	}

	public synchronized List<List<String>> getMessages(){
		return tasks.stream().map(task -> task.messages).collect(Collectors.toList());

	}

	public synchronized String log() {
		return String.format("Tasks size: %s, thread number: %s ", tasks.size(), runningThreads );
	}





}
