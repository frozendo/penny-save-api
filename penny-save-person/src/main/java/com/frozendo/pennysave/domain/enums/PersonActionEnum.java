package com.frozendo.pennysave.domain.enums;

import com.frozendo.pennysave.exceptions.ValueNotFoundException;

import java.util.Arrays;

public enum PersonActionEnum {

    CREATED('C'),
    UPDATED('U');

    private final Character code;

    PersonActionEnum(Character code) {
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

    public static Character convertToPersonActionCode(PersonActionEnum personActionEnum) {
        return Arrays.stream(PersonActionEnum.values())
                .filter(personActionEnum::equals)
                .map(PersonActionEnum::getCode)
                .findFirst()
                .orElseThrow(() -> new ValueNotFoundException("Person Action doesn't correspond to any code"));
    }

    public static PersonActionEnum convertToPersonActionEnum(Character personActionCode) {
        return Arrays.stream(PersonActionEnum.values())
                .filter(item -> item.getCode().equals(personActionCode))
                .findFirst()
                .orElseThrow(() -> new ValueNotFoundException("Code doesn't correspond to any person action"));
    }
}
