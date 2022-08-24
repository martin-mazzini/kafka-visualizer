package com.example.demo.controller;


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
		if (Integer.parseInt(index) < messages.size()) {
			return ProducerThreadPool.getInstance().getMessages().get(Integer.parseInt(index));
		} else {
			return Arrays.asList("Consultando producer out of index");
		}
	}

	@GetMapping("/produce")
	public List<String> produce() {
		return ProducerThreadPool.getInstance().getMessages().stream().flatMap(Collection::stream).collect(Collectors.toList());

	}


	@GetMapping("/produce/log")
	public String log() {
		return ProducerThreadPool.getInstance().log();

	}


}

