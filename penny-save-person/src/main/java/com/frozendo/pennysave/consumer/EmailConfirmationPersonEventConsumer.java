package com.frozendo.pennysave.consumer;

import com.frozendo.pennysave.domain.dto.events.CreatePersonEvent;
import com.frozendo.pennysave.domain.enums.PersonEventEnum;
import com.frozendo.pennysave.domain.enums.PersonQueueEnum;
import com.frozendo.pennysave.exceptions.EmailException;
import com.frozendo.pennysave.exceptions.EntityNotFoundException;
import com.frozendo.pennysave.handler.RabbitDelayedMessageHandler;
import com.frozendo.pennysave.service.EmailConfirmationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
public class EmailConfirmationPersonEventConsumer extends RabbitDelayedMessageHandler {

    private final Logger logger = LoggerFactory.getLogger(EmailConfirmationPersonEventConsumer.class);

    private static final String QUEUE = "email-notification-queue";
    private static final String CONTAINER_FACTORY = "emailConfirmationContainerFactory";

    private final EmailConfirmationService emailConfirmationService;

    public EmailConfirmationPersonEventConsumer(RabbitTemplate rabbitTemplate, EmailConfirmationService emailConfirmationService) {
        super(rabbitTemplate);
        this.emailConfirmationService = emailConfirmationService;
    }

    @RabbitListener(queues = QUEUE, containerFactory = CONTAINER_FACTORY)
    public void consumer(CreatePersonEvent createPersonEvent, Message message) {
        try {
            emailConfirmationService.notificationForPersonCreateEvent(createPersonEvent);
        } catch (Exception exception) {
            logger.error("desgraça da buceta de error que nem sempre aparece {}", exception.getMessage());
            retryEmailConfirmationEvent(createPersonEvent, message);
        }
    }

    private void retryEmailConfirmationEvent(CreatePersonEvent createPersonEvent, Message message) {
        shouldRetryEvent(
                PersonEventEnum.PERSON_DLQ_DIRECT_EXCHANGE.getProperty(),
                PersonQueueEnum.EMAIL_NOTIFICATION_PERSON_EVENT.getDelayedQueue(),
                createPersonEvent,
                message
        );
    }

}
