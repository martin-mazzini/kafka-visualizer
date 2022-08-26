package com.example.demo.consumer;

import java.util.List;
import java.util.concurrent.Future;

class ConsumerRunnableReference {

    private Future task;
    private ConsumerRunnable consumerRunnable;

    public ConsumerRunnableReference(Future task, ConsumerRunnable consumerRunnable) {
        this.task = task;
        this.consumerRunnable = consumerRunnable;
    }

    public List<String> getMessages() {
        return consumerRunnable.getMessages();
    }

    public synchronized Future getTask() {
        return task;
    }


    public ConsumerData getConsumerData() {
        return consumerRunnable.getData();
    }
}
