package com.example.demo.producer;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;
@Data
public class ProducerData {


    private List<String> records  = new ArrayList<>();
    private Long latency;
    private boolean useKey;


}
