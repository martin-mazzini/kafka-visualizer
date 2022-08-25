package com.example.demo;


import com.example.demo.consumer.ConsumerThreadPool;
import com.example.demo.dictionary.Dictionary;
import com.example.demo.producer.ProducerRunnable;
import com.example.demo.producer.ProducerThreadPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;


@SpringBootApplication
public class DemoApplication {

	private Logger logger = LoggerFactory.getLogger(DemoApplication.class.getName());




	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
	}



	@Value(value = "${kafka.topic}")
	private String topicName;

	@Autowired
	private ConsumerThreadPool consumerThreadPool;

	@Autowired
	private ProducerThreadPool producerThreadPool;

	@EventListener(ApplicationReadyEvent.class)
	public void doSomethingAfterStartup() {


		Dictionary.loadData();
		consumerThreadPool.start();
		producerThreadPool.start();


	}


}
