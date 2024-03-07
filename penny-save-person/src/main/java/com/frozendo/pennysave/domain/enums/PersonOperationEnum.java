package com.frozendo.pennysave.domain.enums;

import com.frozendo.pennysave.exceptions.ValueNotFoundException;

import java.util.Arrays;

public enum PersonOperationEnum {

    CREATED('C'),
    UPDATED('U');

    private final Character code;

    PersonOperationEnum(Character code) {
        this.code = code;
    }

    private Character getCode() {
        return this.code;
    }

    public String getEventRoutingKey() {
        if (this.code.equals(UPDATED)) {
            return "";
        }
        return PersonEventEnum.PERSON_CREATE_KEY.getProperty();
    }

    public static Character convertToPersonOperationCode(PersonOperationEnum personOperationEnum) {
        return Arrays.stream(PersonOperationEnum.values())
                .filter(personOperationEnum::equals)
                .map(PersonOperationEnum::getCode)
                .findFirst()
                .orElseThrow(() -> new ValueNotFoundException("Person Operation doesn't correspond to any code"));
    }

    public static PersonOperationEnum convertToPersonOperationEnum(Character personOperationCode) {
        return Arrays.stream(PersonOperationEnum.values())
                .filter(item -> item.getCode().equals(personOperationCode))
                .findFirst()
                .orElseThrow(() -> new ValueNotFoundException("Code doesn't correspond to any person operation"));
    }
}
