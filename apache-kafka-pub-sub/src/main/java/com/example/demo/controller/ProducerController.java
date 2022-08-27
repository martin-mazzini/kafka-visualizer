package com.example.demo.controller;


import com.example.demo.producer.ProducerData;
import com.example.demo.producer.ProducerThreadPool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.example.demo.controller.dto.*;

import java.util.List;

@RestController()
public class ProducerController {

    @Autowired
    private ProducerThreadPool producerThreadPool;


    @GetMapping("/producer")
    public List<ProducerData> produce() {
        return producerThreadPool.getProducerData();
    }


    @PatchMapping("/producer")
    public ResponseEntity<String> updateProducer(@RequestBody UpdateProducerDTO updateProducerDTO) {
        producerThreadPool.updateConsumer(updateProducerDTO.getLatency(), updateProducerDTO.getUseKey());
        return ResponseEntity.ok().build();
    }


}

