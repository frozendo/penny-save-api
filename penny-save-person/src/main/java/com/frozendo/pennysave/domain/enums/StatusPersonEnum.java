package com.frozendo.pennysave.domain.enums;

import com.frozendo.pennysave.exceptions.ValueNotFoundException;

import java.util.Arrays;

public enum StatusPersonEnum {

    PENDING('P'),
    ACTIVE('A'),
    BLOCKED('B');

    private final Character code;

    StatusPersonEnum(Character code) {
        this.code = code;
    }

    private Character getCode() {
        return this.code;
    }

    public static Character convertToStatusCode(StatusPersonEnum statusPerson) {
        return Arrays.stream(StatusPersonEnum.values())
                .filter(statusPerson::equals)
                .map(StatusPersonEnum::getCode)
                .findFirst()
                .orElseThrow(() -> new ValueNotFoundException("Status doesn't correspond to any code"));
    }

    public static StatusPersonEnum convertToStatusEnum(Character statusCode) {
        return Arrays.stream(StatusPersonEnum.values())
                .filter(item -> item.getCode().equals(statusCode))
                .findFirst()
                .orElseThrow(() -> new ValueNotFoundException("Code doesn't correspond to any status"));
    }

}
