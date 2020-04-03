package com.kafka.procedur.consumer.service;

import com.kafka.procedur.consumer.model.Transaction;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class ConsumerService {

    @KafkaListener(topics = "TransactionEvent",containerFactory = "kafkaListenerContainerFactory")
    public void consume(Transaction transaction){
        System.out.println(transaction.toString());
    }

}
