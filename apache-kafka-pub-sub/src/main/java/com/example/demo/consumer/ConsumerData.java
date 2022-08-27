package com.example.demo.consumer;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;
@Data
public class ConsumerData {

    private List<Integer> partitions = new ArrayList<>();
    private List<RecordDTO> records  = new ArrayList<RecordDTO>();
    private String consumerId;
    private String consumerGroup;
    private Long latency;

    public void addPartition(int partition) {
        this.partitions.add(partition);
    }

}
