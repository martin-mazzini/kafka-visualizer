package com.example.demo;


import com.example.demo.consumer.ConsumerThreadPool;
import com.example.demo.dictionary.Dictionary;
import com.example.demo.producer.ProducerThreadPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.kafka.core.KafkaTemplate;

@SpringBootApplication
public class DemoApplication {

	private Logger logger = LoggerFactory.getLogger(DemoApplication.class.getName());


	@Autowired
	private KafkaTemplate<String, String> template;



	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
	}



	@Value(value = "${kafka.topicFivePartition}")
	private String topicName;


	@EventListener(ApplicationReadyEvent.class)
	public void doSomethingAfterStartup() {

		logger.info("Cargando diccionario en memoria");
		Dictionary.loadData();

		logger.info("Inicializando thread pool de consumers");
		ConsumerThreadPool.initialize(topicName, 1000L);
		ConsumerThreadPool.getInstance().start();
		logger.info("Inicializando thread pool de producers");
		ProducerThreadPool.initialize(template, topicName, 1000L);
		ProducerThreadPool.getInstance().start();


		System.out.println("hola");


	}


}
