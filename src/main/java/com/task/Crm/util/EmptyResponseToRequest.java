package com.task.Crm.util;

public class EmptyResponseToRequest extends RuntimeException {
    public EmptyResponseToRequest(String message) {
        super(message);
    }
}
