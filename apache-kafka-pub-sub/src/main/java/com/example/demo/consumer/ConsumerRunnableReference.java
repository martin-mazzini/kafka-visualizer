package com.example.demo.consumer;

import java.util.List;
import java.util.concurrent.Future;

class ConsumerRunnableReference {

    private List<String> messages;
    private Future task;
    private ConsumerRunnable consumerRunnable;

    public ConsumerRunnableReference(List<String> messages, Future task, ConsumerRunnable consumerRunnable) {
        this.messages = messages;
        this.task = task;
        this.consumerRunnable = consumerRunnable;
    }

    public synchronized List<String> getMessages() {
        return messages;
    }



    public synchronized Future getTask() {
        return task;
    }


    public ConsumerRunnable getConsumerRunnable() {
        return consumerRunnable;
    }


}
