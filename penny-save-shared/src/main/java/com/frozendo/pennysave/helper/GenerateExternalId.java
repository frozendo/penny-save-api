package com.frozendo.pennysave.helper;

import java.util.UUID;

public class GenerateExternalId {

    private GenerateExternalId() {}

    public static String generate() {
        return UUID.randomUUID()
                .toString()
                .trim()
                .substring(0, 20)
                .toUpperCase();
    }

}
