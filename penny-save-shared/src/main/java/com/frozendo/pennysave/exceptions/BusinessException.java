package com.frozendo.pennysave.exceptions;

import com.frozendo.pennysave.enums.MessageEnum;

public class BusinessException extends RuntimeException {

    private final String code;

    public BusinessException(MessageEnum message) {
        super(message.getMessage());
        this.code = message.getCode();
    }

    public String getCode() {
        return code;
    }
}
