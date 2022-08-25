package com.example.demo.consumer;

import java.util.List;
import java.util.concurrent.Future;

class ConsumerRunnableReference {

    List<String> messages;
    Future task;
    private ConsumerRunnable consumerRunnable;

    public ConsumerRunnableReference(List<String> messages, Future task, ConsumerRunnable consumerRunnable) {
        this.messages = messages;
        this.task = task;
        this.consumerRunnable = consumerRunnable;
    }


}
