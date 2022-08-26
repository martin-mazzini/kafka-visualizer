package com.example.demo.topics;

import org.apache.kafka.clients.admin.Admin;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;


@Configuration
public class KafkaTopicConfig {

    @Value(value = "${kafka.topic}")
    private String topicName;

    @Value(value = "${kafka.bootstrapAddress}")
    private String bootstrapAddress;

    @Bean
    public Admin kafkaAdmin() {
        Properties properties = new Properties();
        properties.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress);
        Admin admin = Admin.create(properties);
        return admin;
    }

    @Bean
    public NewTopic topic1() {
        return new NewTopic(topicName, 4, (short) 1);
    }



}
