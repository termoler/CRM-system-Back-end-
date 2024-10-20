package com.task.Crm.util;

import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import java.util.List;

public class ExceptionUtil {
    static public StringBuilder getException(BindingResult bindingResult){
        StringBuilder error = new StringBuilder();

        List<FieldError> fieldErrors = bindingResult.getFieldErrors();
        for (FieldError fieldError : fieldErrors) {
            error.append(fieldError.getField()).append(" : ").append(fieldError.getDefaultMessage()).append("\n");
        }

        return error;
    }
}
