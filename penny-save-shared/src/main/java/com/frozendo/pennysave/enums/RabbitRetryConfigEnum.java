package com.frozendo.pennysave.enums;

import java.util.Arrays;

public enum RabbitRetryConfigEnum {
    FIRST_RETRY(1, 5000),
    SECOND_RETRY(2, 10000),
    THIRD_RETRY(3, 30000);

    private final Integer retryNumber;
    private final Integer expirationTime;

    RabbitRetryConfigEnum(int retryNumber, int expirationTime) {
        this.retryNumber = retryNumber;
        this.expirationTime = expirationTime;
    }

    public Integer getRetryNumber() {
        return retryNumber;
    }

    public Integer getExpirationTime() {
        return expirationTime;
    }

    public static String getExpirationTimeForRetry(int retryNumber) {
        var retryConfig =  Arrays.stream(RabbitRetryConfigEnum.values())
                .filter(i -> i.retryNumber == retryNumber)
                .findFirst()
                .orElse(THIRD_RETRY);
        return retryConfig.expirationTime.toString();
    }

}
