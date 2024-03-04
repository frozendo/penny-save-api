package com.frozendo.pennysave.enums;

import com.frozendo.pennysave.exceptions.ValueNotFoundException;

import java.util.Arrays;

public enum YesNoEnum {

    YES('Y'),
    NO('N');

    private final Character code;

    YesNoEnum(Character code) {
        this.code = code;
    }

    private Character getCode() {
        return this.code;
    }

    public static Character convertEnumToCode(YesNoEnum statusPerson) {
        return Arrays.stream(YesNoEnum.values())
                .filter(statusPerson::equals)
                .map(YesNoEnum::getCode)
                .findFirst()
                .orElseThrow(() -> new ValueNotFoundException("Status doesn't correspond to any code"));
    }

    public static YesNoEnum convertCodeToEnum(Character statusCode) {
        return Arrays.stream(YesNoEnum.values())
                .filter(item -> item.getCode().equals(statusCode))
                .findFirst()
                .orElseThrow(() -> new ValueNotFoundException("Code doesn't correspond to any status"));
    }
}
