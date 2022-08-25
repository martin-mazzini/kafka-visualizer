package com.example.demo.controller;

import com.example.demo.consumer.ConsumerThreadPool;
import com.example.demo.producer.ProducerThreadPool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController("")
public class ThreadPoolController {


	@Autowired
	private ConsumerThreadPool consumerThreadPool;


	@GetMapping("/consumer/add")
	public String addConsumer() {
		consumerThreadPool.addConsumer();
		return consumerThreadPool.log();

	}


	@GetMapping("/consumer/remove")
	public String removeConsumer() {
		consumerThreadPool.removeConsumer();
		return consumerThreadPool.log();


	}

	@GetMapping("/producer/add")
	public String addProducer() {

		ProducerThreadPool.getInstance().addProducer();
		return ProducerThreadPool.getInstance().log();

	}


	@GetMapping("/producer/remove")
	public String removeProducer() {
		ProducerThreadPool.getInstance().removeProducer();
		return ProducerThreadPool.getInstance().log();


	}





}
