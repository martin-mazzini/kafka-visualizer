package com.example.demo.topics;

import com.example.demo.consumer.ConsumerFactory;
import org.apache.kafka.clients.admin.Admin;
import org.apache.kafka.clients.admin.ListConsumerGroupOffsetsResult;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.common.TopicPartition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.ExecutionException;

@Component
public class TopicPartitionReader implements Runnable {


    private Logger logger = LoggerFactory.getLogger(TopicPartitionReader.class.getName());

    private BeanFactory beanFactory;
    private Admin admin;
    private KafkaConsumer consumer;
    private ConsumerFactory consumerFactory;

    @Value(value = "${kafka.group.id}")
    private String groupId;

    @Value(value = "${kafka.topic}")
    private String topicName;
    private List<TopicPartitionData> topicData = Collections.synchronizedList(new ArrayList<>());


    public TopicPartitionReader(BeanFactory beanFactory, Admin admin, ConsumerFactory consumerFactory) {
        this.beanFactory = beanFactory;
        this.admin = admin;
        this.consumer = consumerFactory.getConsumer("partitionReader-group");
        this.consumerFactory = consumerFactory;
    }

    @Override
    public void run() {
        readPartitionsData();
        ;
    }

    public List<TopicPartitionData> getPartitionsData() {

        return new ArrayList<>(topicData);

    }

    private void readPartitionsData() {


        consumer.subscribe(Collections.singleton(topicName));


        try {

            while (true) {
                ListConsumerGroupOffsetsResult listConsumerGroupOffsetsResult = admin.listConsumerGroupOffsets(groupId);

                Map<TopicPartition, OffsetAndMetadata> topicPartitionOffsetAndMetadataMap = listConsumerGroupOffsetsResult.partitionsToOffsetAndMetadata().get();
                Set<TopicPartition> topicPartition = topicPartitionOffsetAndMetadataMap.keySet();
                Map<TopicPartition, Long> endOffsets = null;
                endOffsets = consumer.endOffsets(topicPartition);


                ArrayList<TopicPartitionData> topicPartitionData1 = new ArrayList<>();
                for (TopicPartition tp : topicPartition) {
                    /*logger.info("Topic :: {} ,EndOffset :: {}, currentOffset {}",
                            tp.partition(),
                            endOffsets.get(tp),
                            topicPartitionOffsetAndMetadataMap.get(tp));*/
                    TopicPartitionData topicPartitionData = new TopicPartitionData(endOffsets.get(tp), topicPartitionOffsetAndMetadataMap.get(tp),
                            tp.partition());
                    topicPartitionData1.add(topicPartitionData);
                }
                Collections.sort(topicPartitionData1, Comparator.comparing(a -> a.getPartition()));


                this.topicData.clear();
                this.topicData.addAll(topicPartitionData1);

                Thread.sleep(100);
            }


        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

    }


}
