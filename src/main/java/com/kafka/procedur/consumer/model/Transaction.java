package com.kafka.procedur.consumer.model;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class Transaction {

    private String amount;
    private String transactionType;

}
