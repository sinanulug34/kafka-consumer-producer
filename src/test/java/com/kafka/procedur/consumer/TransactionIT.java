package com.kafka.procedur.consumer;


import com.kafka.procedur.consumer.model.Transaction;
import com.kafka.procedur.consumer.service.KafkaProducerService;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.junit.After;
import org.junit.ClassRule;
import org.junit.jupiter.api.Test;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.listener.KafkaMessageListenerContainer;
import org.springframework.kafka.listener.MessageListener;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.rule.EmbeddedKafkaRule;
import org.springframework.kafka.test.utils.ContainerTestUtils;
import org.springframework.kafka.test.utils.KafkaTestUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;


// SPRING RUNNER TEST I SOR
@EmbeddedKafka

public class TransactionIT {
    private static final String TOPIC = "TransactionEvent";

    @ClassRule
    public static EmbeddedKafkaRule embeddedKafka =
            new EmbeddedKafkaRule(1, true, TOPIC);

    private  BlockingQueue<ConsumerRecord<String, Object>> records;

    private  KafkaMessageListenerContainer<String,Object> container;

    private final KafkaProducerService kafkaProducerService;

    public TransactionIT(KafkaProducerService kafkaProducerService) {
        this.kafkaProducerService = kafkaProducerService;
    }


    @Test
    public void it_should_be_sent_message() throws InterruptedException {
        Map<String,Object> config = new HashMap<>(KafkaTestUtils.consumerProps("consumer","false",embeddedKafka.getEmbeddedKafka()));

        DefaultKafkaConsumerFactory<String,Object> consumerFactory = new DefaultKafkaConsumerFactory<>(config,new StringDeserializer(),new JsonDeserializer<>());

        ContainerProperties containerProperties = new ContainerProperties(TOPIC);

        container = new KafkaMessageListenerContainer<>(consumerFactory, containerProperties);
        records = new LinkedBlockingQueue<ConsumerRecord<String, Object>>();

        container
                .setupMessageListener(new MessageListener<String, Object>() {
                    @Override
                    public void onMessage(
                            ConsumerRecord<String, Object> record) {
                                record.toString();
                        records.add(record);
                    }
                });
        container.start();
        ContainerTestUtils.waitForAssignment(container, embeddedKafka.getEmbeddedKafka().getPartitionsPerTopic());
        Transaction messag2 = Transaction.builder().amount("10").transactionType("Auth").build();
        //Transaction message = {\"amount\":\"10₺\",\"transactionType\":\"Auth\"};
        Map<String, Object> configs = new HashMap<>(KafkaTestUtils.producerProps(embeddedKafka.getEmbeddedKafka()));

        kafkaProducerService.send(messag2);


        ConsumerRecord<String, Object> singleRecord = records.poll(100, TimeUnit.MILLISECONDS);
        System.out.println(records);
        //assertThat(singleRecord.value()).isEqualTo("{\"amount\":\"10₺\",\"transactionType\":\"Auth\"}");

    }
    @After
    public void tearDown(){
        container.stop();
    }

}
