package com.example.demo.controller;

import com.example.demo.consumer.ConsumerThreadPool;
import com.example.demo.producer.ProducerThreadPool;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController("")
public class ThreadPoolController {




	@GetMapping("/consumer/add")
	public String addConsumer() {
		ConsumerThreadPool.getInstance().addConsumer();
		return ConsumerThreadPool.getInstance().log();

	}


	@GetMapping("/consumer/remove")
	public String removeConsumer() {
		ConsumerThreadPool.getInstance().removeConsumer();
		return ConsumerThreadPool.getInstance().log();


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
