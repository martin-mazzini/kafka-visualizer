package com.example.demo.controller;


import com.example.demo.producer.ProducerData;
import com.example.demo.producer.ProducerThreadPool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController()
public class ProducerController {

	@Autowired
	private ProducerThreadPool producerThreadPool;



	@GetMapping("/producer")
	public List<ProducerData> produce() {
		return producerThreadPool.getProducerData();
	}



}

