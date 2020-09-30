package com.example.demo.controller;

<<<<<<< HEAD
import com.example.demo.consumer.ConsumerThreadPool;
import com.example.demo.producer.ProducerThreadPool;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
=======
import com.example.demo.consumer.MyTopicConsumer;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
>>>>>>> 0ca2f94157c7365e65b82d509527e932458b1c25

@RestController()
public class ConsumerController {

<<<<<<< HEAD
	@GetMapping("/consume/{index}")
	public List<String> consume(@PathVariable String index) {
		List<List<String>> messages = ConsumerThreadPool.getInstance().getMessages();
		if (Integer.parseInt(index) < messages.size()) {
			return ConsumerThreadPool.getInstance().getMessages().get(Integer.parseInt(index));
		} else {
			return Arrays.asList("Consultando producer out of index");
		}
	}

	@GetMapping("/consume")
	public List<String> consume() {
		return ConsumerThreadPool.getInstance().getMessages().stream().flatMap(Collection::stream).collect(Collectors.toList());

	}

	@GetMapping("/consume/log")
	public String log() {
		return ConsumerThreadPool.getInstance().log();

	}

=======
	private MyTopicConsumer myTopicConsumer;

	public ConsumerController(MyTopicConsumer myTopicConsumer) {
		this.myTopicConsumer = myTopicConsumer;
	}


	@GetMapping("/messages")
	public List<String> getMessages() {
		return myTopicConsumer.getMessages();
	}


>>>>>>> 0ca2f94157c7365e65b82d509527e932458b1c25
}
