package com.example.demo.producer;

import java.util.List;
import java.util.concurrent.Future;

class ProducerRunnableReference {
    private List<String> messages;
    private Future task;
    private ProducerRunnable producerRunnable;

    public ProducerRunnableReference(List<String> messages, Future task, ProducerRunnable producerRunnable) {
        this.messages = messages;
        this.task = task;
        this.producerRunnable = producerRunnable;
    }

    public synchronized List<String> getMessages() {
        return messages;
    }

    public synchronized Future getTask() {
        return task;
    }

    public synchronized ProducerRunnable getProducerRunnable() {
        return producerRunnable;
    }
}
