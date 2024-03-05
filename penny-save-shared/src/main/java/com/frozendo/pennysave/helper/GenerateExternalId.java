package com.frozendo.pennysave.helper;

import java.util.UUID;

public class GenerateExternalId {

    private GenerateExternalId() {}

    public static String generate() {
        return UUID.randomUUID()
                .toString()
                .trim()
                .replace("-", "")
                .substring(0, 20);
    }

}
