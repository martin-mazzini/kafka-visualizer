package com.example.demo.producer;

import com.example.demo.dictionary.Dictionary;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;

import java.util.List;

public class ProducerRunnable implements Runnable {

	private KafkaTemplate<String, String> producer;
	private String topicName;
	private volatile Long latency;
	private List<String> messages;

	private Logger logger = LoggerFactory.getLogger(ProducerRunnable.class.getName());

	public ProducerRunnable(KafkaTemplate<String, String> producer, String topicName, Long latency, List<String> messages) {
		this.producer = producer;
		this.topicName = topicName;
		this.latency = latency;
		this.messages = messages;
	}

	@Override public void run() {

		try {
			while (!Thread.currentThread().isInterrupted()) {
				Thread.sleep(latency);
				String randomWord = Dictionary.getRandomWord();
				ListenableFuture<SendResult<String, String>> sent = producer.send(topicName, randomWord);

				sent.addCallback(new ListenableFutureCallback<SendResult<String, String>>() {

					@Override
					public void onSuccess(SendResult<String, String> result) {
						addMessage(randomWord);
					}
					@Override
					public void onFailure(Throwable ex) {
						System.out.println("Unable to send message=["
								+ randomWord + "] due to : " + ex.getMessage());
					}
				});

			}
		} catch (InterruptedException ex) {

				Thread.currentThread().interrupt();
				logger.info("Producer Runnable interrupted, shuting down");
		}
	}

	private void addMessage(String randomWord) {
		messages.add(randomWord);
	}

	public void changeLatency(Long newLatency) {
		this.latency = newLatency;
	}
}


