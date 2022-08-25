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

    public List<String> getMessages() {
        return messages;
    }

    public Future getTask() {
        return task;
    }

    public ProducerRunnable getProducerRunnable() {
        return producerRunnable;
    }
}
