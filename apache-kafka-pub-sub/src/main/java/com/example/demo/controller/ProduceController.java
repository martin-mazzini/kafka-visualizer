package com.example.demo.controller;


import com.example.demo.consumer.ConsumerThreadPool;
import com.example.demo.producer.ProducerThreadPool;
import org.springframework.beans.factory.annotation.Autowired;
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

	@Autowired
	private ProducerThreadPool producerThreadPool;


	@GetMapping("/produce/{index}")
	public List<String> produce(@PathVariable String index) {
		List<List<String>> messages = producerThreadPool.getMessages();
		if (Integer.parseInt(index) < messages.size()) {
			return producerThreadPool.getMessages().get(Integer.parseInt(index));
		} else {
			return Arrays.asList("Consultando producer out of index");
		}
	}

	@GetMapping("/produce")
	public List<String> produce() {
		return producerThreadPool.getMessages().stream().flatMap(Collection::stream).collect(Collectors.toList());

	}


	@GetMapping("/produce/log")
	public String log() {
		return producerThreadPool.log();

	}


}

