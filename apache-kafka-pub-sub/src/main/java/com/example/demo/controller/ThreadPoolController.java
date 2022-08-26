package com.example.demo.controller;

import com.example.demo.consumer.ConsumerThreadPool;
import com.example.demo.producer.ProducerThreadPool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController("")
public class ThreadPoolController {

	@Autowired
	private ConsumerThreadPool consumerThreadPool;
	@Autowired
	private ProducerThreadPool producerThreadPool;

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

	@GetMapping("/producer/add")
	public String addProducer() {
		producerThreadPool.addProducer();
		return producerThreadPool.log();

	}


	@GetMapping("/producer/remove")
	public String removeProducer() {
		producerThreadPool.removeProducer();
		return producerThreadPool.log();
	}





}
