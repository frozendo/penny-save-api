package com.frozendo.pennysave.domain.enums;

import com.frozendo.pennysave.enums.BusinessMessage;

public enum PersonBusinessMessageEnum implements BusinessMessage {
    EMAIL_DUPLICATED("2001", "Email already exist and cannot be used");

    private final String code;
    private final String message;

    PersonBusinessMessageEnum(String code, String message) {
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
