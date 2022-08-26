package com.example.demo.controller;

import com.example.demo.consumer.ConsumerThreadPool;
import com.example.demo.producer.ProducerThreadPool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController("")
public class ThreadPoolController {

	@Autowired
	private ConsumerThreadPool consumerThreadPool;
	@Autowired
	private ProducerThreadPool producerThreadPool;

	@PutMapping("/consumer/{id}")
	public String addConsumer(@PathVariable String id) {
		consumerThreadPool.addConsumer(id);
		return consumerThreadPool.log();

	}


	@DeleteMapping("/consumer/{id}")
	public String removeConsumer(@PathVariable String id) {
		consumerThreadPool.removeConsumer(id);
		return consumerThreadPool.log();


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
