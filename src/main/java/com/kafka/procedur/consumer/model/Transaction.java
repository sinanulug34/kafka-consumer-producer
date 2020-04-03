package com.kafka.procedur.consumer.model;
public class Transaction {

    private String amount;
    private String transactionType;

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(String transactionType) {
        this.transactionType = transactionType;
    }

    public Transaction() {
    }

    @Override
    public String toString() {
        return "Transaction{" +
                "amount='" + amount + '\'' +
                ", transactionType='" + transactionType + '\'' +
                '}';
    }
    public Transaction(String amount, String transactionType) {
        this.amount = amount;
        this.transactionType = transactionType;
    }
}
