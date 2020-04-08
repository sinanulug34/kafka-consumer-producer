package com.kafka.procedur.consumer;

import com.kafka.procedur.consumer.model.Transaction;
import com.kafka.procedur.consumer.service.KafkaProducerService;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.junit.After;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.listener.KafkaMessageListenerContainer;
import org.springframework.kafka.listener.MessageListener;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.test.rule.EmbeddedKafkaRule;
import org.springframework.kafka.test.utils.ContainerTestUtils;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.testcontainers.containers.GenericContainer;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;


@SpringBootTest(classes = KafkaTestApplication.class)
public class TransactionIT {
    private static final String TOPIC = "TransactionEvent";

    @ClassRule
    public static EmbeddedKafkaRule embeddedKafka =
            new EmbeddedKafkaRule(1, true, TOPIC);

    private  BlockingQueue<ConsumerRecord<String, Object>> records;

    private  KafkaMessageListenerContainer<String,Object> container;


    @Autowired
    private  KafkaProducerService kafkaProducerService;

    @Before
    public void setup(){
        records = new LinkedBlockingQueue<ConsumerRecord<String, Object>>();

        //consume  edilecek topic belirlyoruz.
        ContainerProperties containerProperties = new ContainerProperties(TOPIC);

        Map<String,Object> config = new HashMap<>(KafkaTestUtils.consumerProps("consumer","true",embeddedKafka.getEmbeddedKafka()));


        DefaultKafkaConsumerFactory<String,Object> consumerFactory = new DefaultKafkaConsumerFactory<>(config,new StringDeserializer(),new JsonDeserializer<>());

        //consume edilecek mesajı için listener oluşturuyoruz.
        container = new KafkaMessageListenerContainer<>(consumerFactory, containerProperties);
        container
                .setupMessageListener(new MessageListener<String, Object>() {
                    @Override
                    public void onMessage(
                            ConsumerRecord<String, Object> record) {
                        record.toString();
                        records.add(record);
                    }
                });
        //consume listener ı başlatıyoruz
        container.start();
        //consume edilecek topic lerin partitions ı oluşana kadar bekliyoruz.
        ContainerTestUtils.waitForAssignment(container, embeddedKafka.getEmbeddedKafka().getPartitionsPerTopic());

        @ClassRule
        public static GenericContainer zookeeper = new GenericContainer("confluentinc/cp-zookeeper:4.0.0")
                .withNetwork(network)
                .withNetworkAliases("zookeeper")
                .withEnv("ZOOKEEPER_CLIENT_PORT", "2181");

        @ClassRule
        public static KafkaContainer kafkaContainer = new KafkaContainer()
                .withNetwork(network)
                .withExternalZookeeper("zookeeper:2181");

    }

    @Test
    public void sentMessage() throws InterruptedException {
        Transaction message = Transaction.builder().amount("10").transactionType("Auth").build();
        Map<String, Object> configs = new HashMap<>(KafkaTestUtils.producerProps(embeddedKafka.getEmbeddedKafka()));
        kafkaProducerService.send(message);
        ConsumerRecord<String, Object> singleRecord = records.poll(10, TimeUnit.MILLISECONDS);
        System.out.println(records);
    }
    @After
    public void tearDown(){
        container.stop();
    }

}
