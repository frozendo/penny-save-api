package com.frozendo.pennysave.domain.enums;

public enum PersonQueueEnum {

    EMAIL_NOTIFICATION_PERSON_EVENT("email-notification-queue");

    private final String queue;
    private final String delayedQueue;
    private final String dlqQueue;

    PersonQueueEnum(String queue) {
        this.queue = queue;
        this.delayedQueue = queue.concat("-delayed");
        this.dlqQueue = queue.concat("-dlq");
    }

    public String getQueue() {
        return queue;
    }

    public String getDelayedQueue() {
        return delayedQueue;
    }

    public String getDlqQueue() {
        return dlqQueue;
    }
}
