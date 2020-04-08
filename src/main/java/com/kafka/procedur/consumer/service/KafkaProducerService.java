package com.kafka.procedur.consumer.service;

import com.kafka.procedur.consumer.model.Transaction;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class KafkaProducerService {
    private static final String TOPIC_NAME ="TransactionEvent";

    private final KafkaTemplate<String, Transaction> kafkaTemplate;


    public KafkaProducerService(KafkaTemplate<String, Transaction> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void send(Transaction transactionSendEvent){
        kafkaTemplate.send(TOPIC_NAME,transactionSendEvent);
    }

}
