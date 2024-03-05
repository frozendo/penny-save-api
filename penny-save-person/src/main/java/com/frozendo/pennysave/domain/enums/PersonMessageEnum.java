package com.frozendo.pennysave.domain.enums;

import com.frozendo.pennysave.enums.MessageEnum;

public enum PersonMessageEnum implements MessageEnum {
    EMAIL_DUPLICATED("2001", "Email already exist and cannot be used");

    private final String code;
    private final String message;

    PersonMessageEnum(String code, String message) {
        this.code = code;
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }

    @Override
    public String getCode() {
        return code;
    }
}
