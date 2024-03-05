package com.frozendo.pennysave.enums;

import com.frozendo.pennysave.exceptions.ValueNotFoundException;

import java.util.Arrays;

public enum ApiMessageEnum {

    PERSON_EMAIL_WITH_MANY_CHARACTERS("1001", "person_email_many_characters", "The email field cannot have more than 50 characters"),
    PERSON_EMAIL_INVALID("1002", "person_email_invalid", "Email Invalid! Use a valid email"),
    PERSON_NAME_MANDATORY("1003", "person_name_mandatory", "Please enter a name!"),
    PERSON_NAME_WITH_MANY_CHARACTERS("1004", "person_name_many_characters", "The name field cannot have more than 80 characters"),
    PERSON_BIRTH_DATE_MANDATORY("1005", "person_birth_date_mandatory", "Please enter person birth date!"),
    PERSON_PASSWORD_INVALID("1006", "password_invalid", "Password invalid! Use at least one uppercase letter, one lower case letter, a number and a special characters, with size between 8 and 20");

    private final String code;
    private final String key;
    private final String message;

    ApiMessageEnum(String code, String key, String message) {
        this.code = code;
        this.key = key;
        this.message = message;
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public static ApiMessageEnum getValueByKey(String key) {
        return Arrays.stream(ApiMessageEnum.values())
                .filter(item -> item.key.equals(key))
                .findFirst()
                .orElseThrow(() -> new ValueNotFoundException("API key message not found"));
    }
}
