

# What is this?

This repository contains an application to visualize Kafka producers and consumers in action, play with them and illustrate some concepts related to Kafka. The UI allows you to add/remove consumers and control their latency, among others. The application, including a Kafka broker, can be run with a single docker-compose command.


<img width="949" alt="general_prettier" src="https://user-images.githubusercontent.com/25701657/187057550-327776c4-de0e-4e5e-98af-550ff86d4cae.png">

This document has two sections and assumes some basic familiarity with Kafka. If you are new to it, the following link is a good introduction:  [kafka intro](https://medium.com/inspiredbrilliance/kafka-basics-and-core-concepts-5fd7a68c3193)
 -  To interact with the UI and learn what you can do with it, see the **Using the app** section. 
 -  The **Application section** contains some brief notes about the Java Kafka client and how the app works.


# Table of contents

- [What is this?](#what-is-this)
- [Using the app](#using-the-app)
  - [How to run?](#how-to-run)
  - [What exactly does the docker-compose.yml file include?](#what-exactly-does-the-docker-composeyml-file-include)
  - [How to use the visualizer?](#how-to-use-the-visualizer)
  - [What is the UI actually showing?](#what-is-the-ui-actually-showing)
  - [Consumer groups and topic partitions](#consumer-groups-and-topic-partitions)
  - [Partitions as a unit of parallelism](#partitions-as-a-unit-of-parallelism)
  - [Pub/Sub vs Queue](#pubsub-vs-queue)
  - [Throughput](#throughput)
  - [Offsets and lag](#offsets-and-lag)
  - [Message ordering and keys](#message-ordering-and-keys)
- [Application](#application)
  - [How does the app work?](#how-does-the-app-work)
  - [Multi-threaded consumers](#multi-threaded-consumers)
  - [Polling for  messages](#polling-for--messages)
  - [When does the Java consumer commit offsets?](#when-does-the-java-consumer-commit-offsets)


# Using the app

## How to run?

To run the application, download the project and run the following command in the root folder of the project.

*docker-compose up --build*

Next, navigate to http://localhost:8080/index and you will be presented with the following table on the web browser.

**Note:** if you are **restarting the container** after the first launch, the Kafka container will fail and restart a couple of times before the app starts working (related to this [issue)](https://github.com/wurstmeister/kafka-docker/issues/389). If you don´t want to wait, it´s better to start from scratch again. Delete the container and then build again, as follows:

*docker-compose rm -svf*

*docker-compose up --build*

## What exactly does the docker-compose.yml file include?

When you build and run the docker-compose.yml, the following happens.

 - A **web server** is started on port 8080 (a Java Spring app). This app has two main functionalities.
	 - It hosts the **producer** and **consumers** threads that interact with **one Kafka topic with four partitions.**
	 - It provides the UI to visualize and control the Kafka consumers and producer threads.
 - **One single Kafka broker** is started, along **one single ZooKeeper** instance (needed to run Kafka).

## How to use the visualizer?

## What is the UI actually showing?

When you start the application, you are presented with the following screen.

<img width="950" alt="general" src="https://user-images.githubusercontent.com/25701657/187057600-ea1492dd-468a-44d8-848f-03c0dca9ffeb.png">


This UI is a live visualization of a Kafka producer and Kafka consumers, writing to one topic with four partitions, according to the following diagram.

<img width="938" alt="kafkas_diagram" src="https://user-images.githubusercontent.com/25701657/187057614-e5699e72-f05e-4bba-8f0b-148f77f92aa4.png">




With this diagram in mind, it´s easier to explain what each box is showing.

 - **Producer box**: the words that appear in this box are the messages being sent by the producer to the Kafka topic. The **Latency** box allows you to control the rate at which each message is produced. The **Use Key** checkbox decides if the producer uses a key when sending a message. If it´s checked, the producer uses the number prefixed to the word as a key when sending the record to Kafka.
 - **Consumer boxes**: similarly, the words appearing in these boxes correspond to the words being read by the respective consumer. You can stop a given consumer with the **Remove consumer** button. Inactive consumers are greyed out and can be started with the **Add consumer** button. The **Partitions** row shows the topic partitions which are currently assigned to the given consumer. The **Latency** box allows you to control the rate at which each message is being read.
 - **Topic partitions table**: this table shows the end offset, current offset, and lag of each of the partitions.

With that being said, the following sections give a little  bit more detail and context on how the UI can be used to illustrate some Kafka concepts

**Note:** if you use the "Latency" feature, take into account that it's implemented with a simple Thread.sleep(), without notifying threads of changes in its value. This means that if you set a really high time (like minutes), and then reduce it to milliseconds, you will have to wait for the period to elapse so that the thread resumes and starts processing with the new latency value.



## Consumer groups and topic partitions 

All the consumers in the app are part of the same consumer group (“group-one”). In Kafka, consumer groups control how partitions are assigned to consumers. The rules are as follows:

 - All consumers must belong to a consumer group.
 - All partitions are read by the consumer group.
 - Each consumer within a group reads from exclusive partitions (one consumer can read from multiple partitions, but each partition is read only by one consumer). This rule is illustrated in the below diagram.

![image](https://user-images.githubusercontent.com/25701657/187061049-a924bdc1-16eb-4616-83bc-ac77a95b833e.png)

 
By using the  **Add consumer** and **Remove consumer** buttons, you can see how partitions get re-assigned between the active consumers. This process of moving partitions across consumers is known as **Partition Rebalance**. Partitions assigned to the respective consumer are shown in the **Partitions** row. Kafka will always try to spread the partitions across different consumers (depending on the assignment strategy, you can read more about it here => [kafka-partition-assignment-strategies)](https://medium.com/streamthoughts/understanding-kafka-partition-assignment-strategies-and-how-to-write-your-own-custom-assignor-ebeda1fc06f3)

## Partitions as the unit of parallelism

Note that if you turn on the 5 consumers at the same time, one of them will be idle. In that sense, the amount of partitions limits the maximum amount of concurrent consumers and is therefore the main unit of parallelism in Kafka. Kafka supports increasing the partition number after topic creation, but not decreasing it. 

## Pub/Sub vs Queue

Consumer groups allow you to use Kafka as a **Distributed Queue** or as a **Publish / Subscribe** service.

•  If you want to implement a **Distributed Queue** (each message processed a single time by a single service) you should put all consumers in the same consumer group. Having multiple consumers, in this case, will only allow parallel processing, but each message will get processed only once*. **This is the case for this application**

•  If you want to implement Pub/Sub (one message being broadcasted to multiple services), you would need to create different consumer groups, one for each service. Of course, you can also add multiple consumers in these consumer groups, for parallel processing.

![image](https://user-images.githubusercontent.com/25701657/187061218-a158999e-0963-4191-961a-5c1c5bd42045.png)


*Only once, if we don´t consider the possibility of receiving duplicate messages, in case of at-least-once semantics.

## Throughput

You can play with different amounts of active consumers in combination with different producer and consumer latencies, to see how they affect the resulting throughput and the lag of each partition. The following is an example of a possible configuration that produces some lagging partitions because of different latency values in each consumer, just after a couple of minutes. Partition 3, with a latency of 10 seconds, has the highest lag. 

<img width="623" alt="different_offsets" src="https://user-images.githubusercontent.com/25701657/187057723-703b3877-f6a8-45b5-82be-fd86014f397e.png">


## Offsets and lag

In Kafka, each message within a partition gets an incremental id, called **offset**.
In addition, for each topic being read by a consumer group, Kafka stores the last offsets at which it has been reading, for every partition of the said topic. When a consumer has finished processing data, it should periodically commit the offsets. This allows Kafka to know up until what point a consumer has successfully read a partition. If the consumer dies, it will be able to read back from where it left thanks to the committed offsets. In this application, you can test this by removing and starting one consumer, and simply verifying that it doesn´t replay old data. If you wanted to replay old data, you would do it by resetting offsets, which would allow you to read from the beginning of the topic or any given offset.

The table **Topic partitions** shows the end offset for each partition, and also the current offset at which the consumer group has been reading. The difference between the two is the **lag**, which represents how “far behind” the consumer group is.

![image](https://user-images.githubusercontent.com/25701657/187061449-e5967d0f-e8af-4c97-a59c-c6c7bc2cde96.png)
An example of an up-to-date consumer group.


## Message ordering and keys

The checkbox **Use key** allows you to toggle between sending messages with or without keys. The key is the single digit that is prepended to each word (varying between 0 and 5). If it is active and you see "5-literary" in the producer, this means the message with the word "literary" was sent with the key 5. In Kafka, if a key is provided, then all messages of that key go to the same partition (the key is hashed and determines the target partition). If you don´t, then it's assigned randomly (in a round-robin fashion).
![image](https://user-images.githubusercontent.com/25701657/187061520-ea563514-5d60-4538-a1e6-83bdedc28cfd.png)


If the **Use key** feature is active, you should notice that each partition receives a subset of words with the same key. In the example below, consumer 1 is receiving only words with keys 2 and 4. Because it was assigned partitions 0 and 1, we know that keys 2 and 4 must be going to these partitions. Consumer 2 is receiving key 5 in partition 2. Lastly, consumer 3 is receiving keys 1 and 3 in partition 3. 

<img width="627" alt="producing with keys" src="https://user-images.githubusercontent.com/25701657/187057763-020221a6-888c-44db-96b2-39e4b3349c0e.png">

If the **Use key** feature is inactive, then you should notice that each partition receives messages that can contain any key, like the example below.

<img width="629" alt="producing_without_keys" src="https://user-images.githubusercontent.com/25701657/187058201-8ffbe569-85d5-427f-9fd4-3cc85ce13b81.png">


Kafka doesn´t guarantee order across partitions. It only guarantees that within a particular partition, messages are going to be processed in the order they were sent. This means that all messages of a given key are going to be consumed in order because they are all going to a single partition (and can´t be consumed by more than one consumer, as explained in the Consumer groups section). This is an important feature of Kafka. A real-world example would be processing GPS data of vehicle location. In that scenario, we could imagine that receiving each car´s data in order would be useful (to track the car´s position on a map, for example), but we wouldn´t need global ordering of all the car´s positions. In that case, we could send the GPS coordinates with a car ID as the key. 


# Application

## How does the app work?

The application uses the Kafka Java client to communicate with Kafka. The key abstractions provided by the client are the following.
- **Producer**: to send messages to a given topic. The Producer class is **thread-safe**, so there is no need to create more than one Producer object per application. When you use the producer to send a message, you specify the topic name, the message, and optionally, a key.
- **Consumer**: to receive (poll) messages from Kafka. When you create the Consumer, you have to specify the consumer group that it will join. To be able to add and remove consumers dynamically, the application uses a Thread Pool. Unlike the Producer class, the Consumer class is not **thread-safe**, and that´s the reason why one Consumer object is created for each consumer thread (concurrent access to the Consumer object results in a ConcurrentModificationException being thrown).
- **Admin:** it allows you to do some tasks like creating topics and also retrieving information about consumer groups.


## Multi-threaded consumers
In this application, one thread per consumer approach was used. It´s worth mentioning that in real systems there is another possible approach, namely decoupling the consumption threads from the message processing threads. This option allows independently scaling of the number of consumers and processors and makes it possible to have a single consumer that feeds many processor threads, avoiding any limitation on partitions. This is explained on the following link in the section called "Multi-threaded" processing:
[Multi-thread processing](https://kafka.apache.org/25/javadoc/org/apache/kafka/clients/consumer/KafkaConsumer.html)

## Polling for  messages

When you poll for messages, you send a timeout parameter. If there are records available, it returns immediately. Otherwise, the Consumer will await the passed timeout. If the timeout expires, an empty record set will be returned. Java Kafka consumers normally poll in batches, receiving multiple messages at once. In this application, the max amount of messages fetched has been limited to 1, so that the configured **Latency** is a per message value.


## When does the Java consumer commit offsets?

When using the Java consumer API (as in this application), by default consumers will commit offsets automatically **after** the message is processed. This results in an  **at-least-once** semantic, and consumers should therefore be idempotent. With auto-commit, consumers commit the offsets when they call the poll() method after some configurable time window has elapsed. This is why you should be sure all messages have successfully been processed before calling the poll method again (or accept possible data loss). Alternatively, offsets can be manually committed (by disabling the auto-commit configuration and calling the commit method manually).
