package com.task.Crm.util.TransactionException;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TransactionErrorResponse {
    private String message;
    private long timestamp;
    public TransactionErrorResponse(String message, long timestamp) {
        this.message = message;
        this.timestamp = timestamp;
    }
}