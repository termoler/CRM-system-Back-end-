package com.task.Crm.util.TransactionException;

public class TransactionNotCreatedException extends RuntimeException{
    public TransactionNotCreatedException(String msg) {
        super(msg);
    }
}
