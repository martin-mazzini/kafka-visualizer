package com.example.demo.producer;


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


    public  void update(Long latency, Boolean useKey) {
       producerRunnable.update(latency, useKey);

    }
}
