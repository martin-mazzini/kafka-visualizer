
# What is this?

This repository contains an application to visualize Kafka producers and consumers in action, in order to play with it and illustrate some concepts. The application, including a Kafka broker, can be run with a single docker-compose command (see How to run).


The UI allows you to add/remove consumers, control the latency of producer and consumers, among others. For using the app directly, see the Using the app section. That sections assumes basic familiarity with Kafka concepts. For a quick theortical explanation of some of these concepts, you can see the What is Kafka section.

# Using the app

## How to run?

To run the application, download the project and run the following command in the root folder of the project.

docker-compose up --build

Next, navigate to http://localhost:8080/index and you will be presented with the following table on the web browser.

## What exactly does the docker-compose.yml file include?

When you build and run the docker-compose.yml, the following happens.

 - A **web server** is started on port 8080 (a Java Spring app). This app has two main functionalities.
	 - It hosts the **producer** and **consumers** threads that interact with **one Kafka topic with four partitions.**
	 - It provides the UI to visualize and control the Kafka consumers and producer threads.
 - **One single Kafka broker** is started, along **one single ZooKeeper** instance (needed to run Kafka).

## How to use the visualizer?

### What is the UI actually showing?

When you start the application, you are presented with the following screen.


This UI is a live visualization of a Kafka producer and Kafka consumers, writing to one topic with four partitions, according to the following diagram.



With this diagram in mind, it´s easy to explain what each box is showing.

 - **Producer box**: the words that appear in this box are the messages being sent by the producer to the Kafka topic. The **Latency** box allows you to control the rate at which each message is produced. The **Use Key** checkbox decides if the producer uses a key when sending message. If it´s checked, the producer uses the number prefixed to the word as a key when sending the record to Kafka.
 - **Consumer boxes**: in a similar way, the words appearing in these boxes correspond to the words being read by the respective consumer. You can stop a given consumer with the **Remove consumer** button. Inactive consumers are greyed out, and can be started with the **Add consumer** button. The **Partitions** row show the topic partitions which are currently assigned to the given consumer. The **Latency** box allows you to control the rate at which each message is being read.
 - **Topic partitions table**: this table shows the end offset, current offset, and lag of each of the partitions.

The following sections details how the UI can be used to illustrate some interesting Kafka concepts

### Consumer groups and topic partitions 

All the consumers in the app are part of the same consumer-group (“group-one”). Consumer groups control how partitions are assigned to consumers. The rules are as follows:

 - All consumers must belong to a consumer group.
 - Each consumer within a group reads from exclusive partitions (one consumer can read from multiple partitions, but each partition is read only by one consumer)
 
By using the  **Add consumer** and **Remove consumer** buttons, you can see how partitions get re-assigned between the active consumers. Partitions assigned to the respective consumer are shown in the **Partitions** row. Kafka will always try to spread the partitions across different consumers (depending on the assignment strategy, you can read more about it here => [kafka-partition-assignment-strategies)](https://medium.com/streamthoughts/understanding-kafka-partition-assignment-strategies-and-how-to-write-your-own-custom-assignor-ebeda1fc06f3)

### Partitions as unit of parallelism

Note that if you turn on the 5 consumers at the same time, one of them will be idle, as shown below. In that sense, the amount of partitions limits the maximum amount of concurrent consumers, and is therefore the main unit of parallelism in Kafka. Kafka supports increasing the partition number after topic creation, but not decreasing it.

### Pub/Sub vs Queue

Consumer groups allows you to control if you use Kafka as a **Distributed Queue** or as a **Publish / Subscribe** service.

•  If you want to implement a **Distributed Queue** (each message processed a single time by a single service) you should put all consumers in the same consumer-group. Having multiple consumers in this case will only allow paralell processing, but each message will get processed only once (ignoring duplicate message related to the at-least-one semantics guarantees). **This is the example shown in this application**
•  If you want to implement Pub/Sub (one message being broadcasted to multiple services), you would need to create different consumer groups, one for each service. Of course, you can also add multiple consumers in this consumer groups, for paralell processing.



### Througphut

You can play with different amount of active consumers and producer in combination with different consumer and producer latencies, to see how they affect the resulting throughput, and the lag of each partition (see following section).

The **Latency** input field controls how long each producer or consumer takes to publish or consume one single message, respectively. 
As a side note, Java Kafka consumers normally poll multiple messages at once when they call the poll() method. In this application, the max amount of messages fetched has been limited to 1, so that the configured **Latency** stays consistent.

### Offsets and lag

In Kafka, each message within a partition gets an incremental id, called **offset**.
In addition, for each consumer group, Kafka stores the last offsets at which it has been reading, for every partition. When a consumer has finished processing data, it should periodically be commitig the offsets. This allows Kafka to know up until what point a consumer has successfully read a partition. If the consumer dies, it will be able to read back from where it left thanks to the commited offsets. In this application, you can test this by removing and starting one consumer, and simply verifying that it doesn´t replay old data. If you wanted to replay old data, you would do it by resetting offsets, which would allow you to read from the beginning of the topic or from any given offset.

The table **Topic partitions** shows the end offset for each partition, and also the current offset at which the consumer group has been reading. The difference between the two is the **lag**, which represents how “far behind” the consumer group is.


#### When does the Java consumer commit offsets?

When using the Java consumer API (as in this application), by default consumers will commit offsets automatically **after** the message is processed. This results  in an  **at-least-once** semantic, and consumers should should therefore be idempotent. With auto-commit, consumers commit the offsets when they call the poll() method after some configurable time window has elapsed. This is why you should be sure all messages have successfully been processed before calling poll again (or accept possible data loss). Alternatively, offsets can be manually commited (by disabling auto-commit configuration and calling the respective method).

### Message ordering and keys

The checkbox **Use key** allows you to toggle between sending messages with or without keys. For each word, it's key is the single digit which prepended to it (varying between 0 and 5). 

If the **Use key** feature is active, you should notice that each partition receives a subset of words that with the same key. In this example below, partition 1 is receiving words with keys 0,1, and partition 2 is receibing keys 2,3,4. Kafka will guarantee that all the messages sent for each key are processed in order. 

A real case scenario would be keys representing the id of a car, and messages with its position. In this case, because the order is guaranteed for each car, the 

Two important Kafka features must be mentioned.
Kafka guarantees Messages withtin each partition are ordered. Order is guaranteed only within a partition, not across partitions. If you want global order, then you can only have one partition.
When you write to a topic, data is assigned randomly to a partition unless a key is provided (it is sent round robin). If a key is provided, then all messages of that key go to the same partition (key is hashed and determines the target partition). Data is read in order (from low to high offset) within each partition.

### Rebalancing
When you add or remove a partition, you could see that the partitions assigned to each consumer dissapear for a brief period of time.

### Hot partitions
