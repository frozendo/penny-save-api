package com.frozendo.pennysave.exceptions;

import com.frozendo.pennysave.enums.BusinessMessage;

public class BusinessException extends RuntimeException {

    private final String code;

    public BusinessException(BusinessMessage message) {
        super(message.getMessage());
        this.code = message.getCode();
    }

    public String getCode() {
        return code;
    }
}
