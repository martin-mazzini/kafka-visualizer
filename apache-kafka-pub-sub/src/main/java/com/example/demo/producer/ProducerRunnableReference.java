package com.example.demo.producer;

import com.example.demo.consumer.ConsumerData;

import java.util.List;
import java.util.concurrent.Future;

class ProducerRunnableReference {

    private Future task;
    private ProducerRunnable producerRunnable;

    public ProducerRunnableReference(Future task, ProducerRunnable producerRunnable) {
        this.task = task;
        this.producerRunnable = producerRunnable;
    }


    public synchronized Future getTask() {
        return task;
    }

    public synchronized ProducerRunnable getProducerRunnable() {
        return producerRunnable;
    }


    public ProducerData getProducerData() {
        return producerRunnable.getData();
    }



}
