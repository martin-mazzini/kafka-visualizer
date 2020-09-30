package com.example.demo.consumer;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class MyTopicConsumer {

	private final List<String> messages = new ArrayList<>();

	@KafkaListener(topics = "${kafka.testTopic}", groupId = "kafka")
	public void listen(String message) {
		synchronized (messages) {
			messages.add(message);
		}
	}

	public List<String> getMessages() {
		return messages;
	}

}