package net.elbakay.backend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class KafkaProducerService {

    private static final String TOPIC = "task-events";

    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

    public void sendTaskEvent(String action, net.elbakay.backend.dto.TaskDTO task) {
        System.out.println("Sending " + action + " event to Kafka");
        net.elbakay.backend.dto.TaskEvent event = new net.elbakay.backend.dto.TaskEvent(action, task);
        kafkaTemplate.send(TOPIC, event); 
    }
}
