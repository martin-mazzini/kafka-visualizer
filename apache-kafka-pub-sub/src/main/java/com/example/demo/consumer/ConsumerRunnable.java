package com.example.demo.consumer;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.errors.WakeupException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.Collections;
import java.util.List;

public class ConsumerRunnable implements Runnable {

	private final Long latency;
	private KafkaConsumer<String, String> consumer;
	private List<String> messages;
	private String topicName;
	private Logger logger = LoggerFactory.getLogger(ConsumerRunnable.class.getName());

	public ConsumerRunnable(List<String> messages, KafkaConsumer consumer, String topicName, Long latency) {
		this.consumer = consumer;
		this.messages = messages;
		this.topicName = topicName;
		this.latency = latency;

	}

	@Override public void run() {

		consumer.subscribe(Collections.singleton(topicName));


		try {
			while (!Thread.currentThread().isInterrupted()) {
				ConsumerRecords<String, String> records =
						consumer.poll(Duration.ofMillis(100)); // new in Kafka 2.0.0

				for (ConsumerRecord<String, String> record : records) {
					Thread.sleep(100);
					//					logger.info((String.format("Consuming from thread %s" , Thread.currentThread().getName())));
					//					logger.info("Key: " + record.key() + ", Value: " + record.value());
					//					logger.info("Partition: " + record.partition() + ", Offset:" + record.offset());
					addMessage(record.value());
				}

			}
		} catch (WakeupException e) {
			logger.info("Received shutdown signal!");
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			logger.info("Received shutdown signal!");
		} finally {
			try {
				consumer.close();
			}catch (Exception ex){
				//do nothing, cierra con una excepcion x un bug
			}

		}

	}

	private synchronized void addMessage(String value) {
		this.messages.add(value);
	}

}
