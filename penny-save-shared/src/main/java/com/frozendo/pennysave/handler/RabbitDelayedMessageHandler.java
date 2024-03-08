package com.frozendo.pennysave.handler;

import org.springframework.amqp.AmqpRejectAndDontRequeueException;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import static com.frozendo.pennysave.enums.RabbitRetryConfigEnum.getExpirationTimeForRetry;

public abstract class RabbitDelayedMessageHandler {

    private static final String X_RETRY_MESSAGE_HEADER = "x-retry";

    private final RabbitTemplate rabbitTemplate;

    protected RabbitDelayedMessageHandler(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    protected void shouldRetryEvent(String exchange, String queue, Object eventObject, Message message) {
        var containKeyRetry = message.getMessageProperties().getHeaders().containsKey(X_RETRY_MESSAGE_HEADER);
        if (!containKeyRetry) {
            sendMessageToDelayedQueue(exchange, queue, eventObject, 1);
            return;
        }
        var xRetry = checkIfArchivedMaxRetry(message);
        sendMessageToDelayedQueue(exchange, queue, eventObject, xRetry);
    }

    private int checkIfArchivedMaxRetry(Message message) {
        var xRetry = Integer.parseInt(message.getMessageProperties().getHeader(X_RETRY_MESSAGE_HEADER));
        xRetry++;
        if (xRetry > 3) {
            throw new AmqpRejectAndDontRequeueException("Event max retries achieved!");
        }
        return xRetry;
    }

    private void sendMessageToDelayedQueue(String exchange, String queue, Object eventObject, int retryNumber) {
        rabbitTemplate.convertAndSend(exchange,
                queue,
                eventObject,
                delayedPostProcessor(retryNumber)
        );
    }

    private MessagePostProcessor delayedPostProcessor(int retryNumber) {
        return m -> {
            m.getMessageProperties().setExpiration(getExpirationTimeForRetry(retryNumber));
            m.getMessageProperties().setHeader(X_RETRY_MESSAGE_HEADER, retryNumber);
            return m;
        };
    }

}
