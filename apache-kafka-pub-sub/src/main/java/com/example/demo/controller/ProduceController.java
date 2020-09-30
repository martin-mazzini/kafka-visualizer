package com.example.demo.controller;

<<<<<<< HEAD
import com.example.demo.consumer.ConsumerThreadPool;
import com.example.demo.producer.ProducerThreadPool;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@RestController()
public class ProduceController {


	@GetMapping("/produce/{index}")
	public List<String> produce(@PathVariable String index) {
		List<List<String>> messages = ProducerThreadPool.getInstance().getMessages();
		if (Integer.parseInt(index) < messages.size()){
		return ProducerThreadPool.getInstance().getMessages().get(Integer.parseInt(index));
		}
		else{
			return Arrays.asList("Consultando producer out of index");
		}
	}

	@GetMapping("/produce")
	public List<String> produce() {
		return ProducerThreadPool.getInstance().getMessages().stream().flatMap(Collection::stream).collect(Collectors.toList());

	}



	@GetMapping("/produce/log")
	public String  log() {
		return ProducerThreadPool.getInstance().log();

	}



=======
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController()
public class ProduceController {

	//	https://www.baeldung.com/spring-kafka
	// USAR CALLBACKS

	//https://stackoverflow.com/questions/62339968/stop-kafkalistener-spring-kafka-consumer-after-it-has-read-all-messages-till suspender
	private KafkaTemplate<String, String> template;


	@Value(value = "${kafka.testTopic}")
	private String testTopic;

	public ProduceController(KafkaTemplate<String, String> template) {
		this.template = template;
	}


	@GetMapping("/produce")
	public void produce(@RequestParam String message) {
		template.send(testTopic, message);
	}
>>>>>>> 0ca2f94157c7365e65b82d509527e932458b1c25
}
