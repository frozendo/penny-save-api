package com.frozendo.pennysave.domain.enums;

public enum PersonEventEnum {

    PERSON_DIRECT_EXCHANGE("person-direct-exchange"),
    PERSON_CREATE_KEY("person-created");

    private final String property;

    PersonEventEnum(String property) {
        this.property = property;
    }

    public String getProperty() {
        return property;
    }
}
