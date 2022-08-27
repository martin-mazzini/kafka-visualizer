package com.example.demo.controller;


import com.example.demo.consumer.ConsumerThreadPool;
import com.example.demo.consumer.ConsumerData;
import com.example.demo.controller.dto.UpdateConsumerDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;


@RestController()
public class ConsumerController {


	@Autowired
	private ConsumerThreadPool consumerThreadPool;


	@GetMapping("/consumer")
	public List<ConsumerData> consumeData() {
		return consumerThreadPool.getConsumerData();

	}


	@PutMapping("/consumer/{id}")
	public ResponseEntity<String> addConsumer(@PathVariable String id) {
		if (consumerThreadPool.addConsumer(id)){
			return ResponseEntity.ok().build();
		}else{
			return ResponseEntity.badRequest().build();
		}


	}

	@DeleteMapping("/consumer/{id}")
	public ResponseEntity<String> removeConsumer(@PathVariable String id) {
		if (consumerThreadPool.removeConsumer(id)){
			return ResponseEntity.ok().build();
		}else{
			return ResponseEntity.badRequest().build();
		}
	}

	@PatchMapping("/consumer/{id}")
	public ResponseEntity<String> updateProducer(@PathVariable String id, @RequestBody UpdateConsumerDTO updateConsumerDTO) {
		consumerThreadPool.updateConsumer(id, updateConsumerDTO.getLatency());
		return ResponseEntity.ok().build();
	}


/*	@GetMapping("/consume/log")
	public String log() {
		return consumerThreadPool.log();
	}


	@GetMapping("/consumer/plainmessages")
	public List<String> consume() {
		return consumerThreadPool.getMessages().stream().flatMap(Collection::stream).collect(Collectors.toList());
	}


	@GetMapping("/consume/{index}")
	public List<String> consume(@PathVariable String index) {
		List<List<String>> messages = consumerThreadPool.getMessages();
		if (Integer.parseInt(index) < messages.size()) {
			return consumerThreadPool.getMessages().get(Integer.parseInt(index));
		} else {
			return Arrays.asList("Consultando producer out of index");
		}
	}*/


}
