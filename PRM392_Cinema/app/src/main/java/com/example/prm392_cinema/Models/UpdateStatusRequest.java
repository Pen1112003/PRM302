package com.example.prm392_cinema.Models;

public class UpdateStatusRequest {
    private String transactionId;

    public UpdateStatusRequest(String transactionId) {
        this.transactionId = transactionId;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }
}
