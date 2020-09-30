package com.example.demo.producer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;

import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

public class ProducerThreadPool {

	private static final Integer MAX_N_THREADS = 5;
	private Integer runningThreads = 0;
	private Long latency;
	private KafkaTemplate<String, String> template;
	private Deque<TaskAndMessages> tasks = new LinkedList<>();
	private ExecutorService threadPool = Executors.newFixedThreadPool(MAX_N_THREADS);
	private String topicName;
	private static ProducerThreadPool producerThreadPool;


	private Logger logger = LoggerFactory.getLogger(ProducerThreadPool.class.getName());



	private ProducerThreadPool() {
	}

	public static ProducerThreadPool initialize(KafkaTemplate<String, String> template, String topicName, Long latency) {
		producerThreadPool = new ProducerThreadPool();
		producerThreadPool.template = template;
		producerThreadPool.topicName = topicName;
		producerThreadPool.latency = latency;
		return producerThreadPool;
	}

	public static ProducerThreadPool getInstance() {
		return producerThreadPool;
	}

	public synchronized String log() {
		return String.format("Tasks size: %s, thread number: %s ", tasks.size(), runningThreads );
	}

	public synchronized void start() {
		TaskAndMessages task1 = createProducerRunnable();
		tasks.addFirst(task1);
		runningThreads = runningThreads + 1;
		TaskAndMessages task2 = createProducerRunnable();
		tasks.addFirst(task2);
		runningThreads = runningThreads + 1;
	}

	private TaskAndMessages createProducerRunnable() {
		List<String> messages = new ArrayList<>();
		ProducerRunnable producerRunnable = new ProducerRunnable(template, topicName, latency, messages);
		Future<?> future = threadPool.submit(producerRunnable);
		TaskAndMessages task = new TaskAndMessages(messages, future, producerRunnable);
		return task;
	}



	public synchronized boolean removeProducer(){
		TaskAndMessages poll = tasks.poll();
		if (poll == null){
			logger.info("No quedan mas producers para parar");
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
			logger.info("ProducerRunnable cancelado correctamente");
			runningThreads = runningThreads - 1;
			return true;
		}else {
			return false;
		}

	}


	public synchronized boolean addProducer(){
		if (runningThreads == MAX_N_THREADS){
			logger.info("Maximo nro de threads alcanzado");
			return false;
		}else {
			TaskAndMessages task = createProducerRunnable();
			runningThreads = runningThreads + 1;
			tasks.add(task);
			logger.info("ProducerRunnable submit correcto");
			return true;
		}
	}


	public synchronized void changeLatency(Long newLatency){
		this.latency = latency;
		for (TaskAndMessages tasks: this.tasks){
			tasks.producerRunnable.changeLatency(latency);
		}
	}


	private class TaskAndMessages {
		List<String> messages;
		Future task;
		private ProducerRunnable producerRunnable;

		public TaskAndMessages(List<String> messages, Future task, ProducerRunnable producerRunnable) {
			this.messages = messages;
			this.task = task;
			this.producerRunnable = producerRunnable;
		}

	}

	public synchronized void getMessage(){
		List<List<String>> messages = tasks.stream().map(task -> task.messages).collect(Collectors.toList());
	}



	public synchronized List<List<String>> getMessages(){
		return tasks.stream().map(task -> task.messages).collect(Collectors.toList());

	}



}
