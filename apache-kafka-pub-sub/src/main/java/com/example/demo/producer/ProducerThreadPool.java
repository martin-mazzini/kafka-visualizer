package com.example.demo.producer;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Collectors;
@Component
public class ProducerThreadPool {


	private Logger logger = LoggerFactory.getLogger(ProducerThreadPool.class.getName());


	private static final Integer MAX_N_THREADS = 5;
	private Integer runningThreads = 0;
	private Long latency = 1000L;
	private Deque<ProducerRunnableReference> tasks = new LinkedList<>();
	private ExecutorService threadPool = Executors.newFixedThreadPool(MAX_N_THREADS);
	@Value(value = "${kafka.topic}")
	private String topicName;
	private KafkaProducer<String, String> producer;

	public ProducerThreadPool(KafkaProducer<String, String> producer) {
		this.producer = producer;
	}

	public synchronized String log() {
		return String.format("Tasks size: %s, thread number: %s ", tasks.size(), runningThreads );
	}

	public synchronized void start() {
		createProducer();
		createProducer();
	}

	private void createProducer() {
		ProducerRunnableReference task1 = createProducerRunnable();
		tasks.addFirst(task1);
		runningThreads++;
	}

	private ProducerRunnableReference createProducerRunnable() {
		List<String> messages = new ArrayList<>();
		ProducerRunnable producerRunnable = new ProducerRunnable(producer, topicName, latency, messages);
		Future<?> future = threadPool.submit(producerRunnable);
		ProducerRunnableReference task = new ProducerRunnableReference(messages, future, producerRunnable);
		return task;
	}



	public synchronized boolean removeProducer(){
		ProducerRunnableReference poll = tasks.poll();
		if (poll == null){
			logger.info("No quedan mas producers para parar");
			return false;
		}
		poll.getTask().cancel(true);
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
			Thread.currentThread().interrupt();
		}
		if (poll.getTask().isCancelled()){
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
			ProducerRunnableReference task = createProducerRunnable();
			runningThreads = runningThreads + 1;
			tasks.add(task);
			logger.info("ProducerRunnable submit correcto");
			return true;
		}
	}


	public synchronized void changeLatency(Long newLatency){
		this.latency = latency;
		for (ProducerRunnableReference tasks: this.tasks){
			tasks.getProducerRunnable().changeLatency(latency);
		}
	}


	public synchronized void getMessage(){
		List<List<String>> messages = tasks.stream().map(task -> task.getMessages()).collect(Collectors.toList());
	}



	public synchronized List<List<String>> getMessages(){
		return tasks.stream().map(task -> task.getMessages()).collect(Collectors.toList());

	}



}
