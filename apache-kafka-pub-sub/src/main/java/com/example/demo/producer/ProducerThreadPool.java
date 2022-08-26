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
	private Deque<ProducerRunnableReference> producerRunnables = new LinkedList<>();
	private ExecutorService threadPool = Executors.newFixedThreadPool(MAX_N_THREADS);
	@Value(value = "${kafka.topic}")
	private String topicName;
	private KafkaProducer<String, String> producer;

	public ProducerThreadPool(KafkaProducer<String, String> producer) {
		this.producer = producer;
	}


	public synchronized void start() {
		createProducer();
	}

	private synchronized void createProducer() {
		ProducerRunnableReference task1 = createProducerRunnable();
		producerRunnables.addFirst(task1);
		runningThreads++;
	}

	private ProducerRunnableReference createProducerRunnable() {
		List<String> messages = new ArrayList<>();
		ProducerRunnable producerRunnable = new ProducerRunnable(producer, topicName, latency, messages);
		Future<?> future = threadPool.submit(producerRunnable);
		ProducerRunnableReference task = new ProducerRunnableReference(future, producerRunnable);
		return task;
	}



	public synchronized boolean removeProducer(){
		ProducerRunnableReference producer = producerRunnables.poll();
		if (producer == null){
			logger.info("No producers for cancellation");
			return false;
		}
		producer.getTask().cancel(true);
		runningThreads--;
		return true;
	}


	public synchronized boolean addProducer(){
		if (runningThreads == MAX_N_THREADS){
			logger.info("Max number of producers");
			return false;
		}else {
			createProducer();
			logger.info("Producer created succesfully");
			return true;
		}
	}


	public synchronized void changeLatency(Long newLatency){
		this.latency = newLatency;
		for (ProducerRunnableReference tasks: this.producerRunnables){
			tasks.getProducerRunnable().changeLatency(latency);
		}
	}




	public synchronized List<ProducerData> getProducerData() {
		return producerRunnables.stream().map(task -> task.getProducerData()).collect(Collectors.toList());
	}

}
