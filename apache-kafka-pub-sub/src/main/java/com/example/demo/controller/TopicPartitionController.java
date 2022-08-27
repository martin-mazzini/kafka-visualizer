package com.example.demo.controller;


import com.example.demo.topics.TopicPartitionData;
import com.example.demo.topics.TopicPartitionReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;


@RestController()
public class TopicPartitionController {


	@Autowired
	private TopicPartitionReader topicPartitionReader;


	@GetMapping("/topicpartitions")
	public List<TopicPartitionData> consumeData() {
		 return topicPartitionReader.getPartitionsData();

	}

}
