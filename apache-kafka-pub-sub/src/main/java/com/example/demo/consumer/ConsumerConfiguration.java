package com.example.demo.consumer;


import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;


import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

@Configuration
public class ConsumerConfiguration {





    @Value(value = "${kafka.bootstrapAddress}")
    private String KAFKA_BROKER;


    @Value(value = "${kafka.group.id}")
    private String groupId;


    @Bean
    @Scope("prototype")
    public KafkaConsumer<String, String> getConsumer(){
        KafkaConsumer<String, String> consumer;
        Properties properties = new Properties();

        properties.setProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG,KAFKA_BROKER);
        properties.setProperty(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        properties.setProperty(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        properties.setProperty(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        consumer = new KafkaConsumer<String, String>(properties);
        return consumer;
    }


}
