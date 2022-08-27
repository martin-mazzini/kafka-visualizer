package com.example.demo.topics;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TopicPartitionData {


    private Long endOffset;
    private long current;
    private int partition;
    private long lag;

    public TopicPartitionData(Long endOffset, OffsetAndMetadata offsetAndMetadata, int partition) {
        this.endOffset = endOffset;
        this.current = offsetAndMetadata.offset();
        this.partition = partition;
        this.lag = this.endOffset - current;
    }


}
