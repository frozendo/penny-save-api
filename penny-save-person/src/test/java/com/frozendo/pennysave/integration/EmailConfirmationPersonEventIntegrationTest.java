package com.frozendo.pennysave.integration;

import com.frozendo.pennysave.PersonModuleIntegrationTest;
import com.frozendo.pennysave.domain.dto.events.CreatePersonEvent;
import com.frozendo.pennysave.domain.enums.PersonActionEnum;
import com.frozendo.pennysave.domain.enums.PersonQueueEnum;
import com.frozendo.pennysave.enums.YesNoEnum;
import com.frozendo.pennysave.repository.EmailConfirmationRepository;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;

import java.time.Duration;

import static com.frozendo.pennysave.domain.enums.PersonEventEnum.PERSON_CREATE_KEY;
import static com.frozendo.pennysave.domain.enums.PersonEventEnum.PERSON_DIRECT_EXCHANGE;
import static com.frozendo.pennysave.enums.RabbitRetryConfigEnum.FIRST_RETRY;
import static com.frozendo.pennysave.enums.RabbitRetryConfigEnum.THIRD_RETRY;

@Sql(value = {"/scripts/person.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(value = {"/scripts/clean.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
class EmailConfirmationPersonEventIntegrationTest extends PersonModuleIntegrationTest {

    private static final String TONY_STARK_EMAIL = "tony@stark.com";
    private static final String TONY_STARK_NAME = "Tony Stark";
    private static final String TONY_STARK_EXTERNAL_ID = "e94ebf6b1d205ddc9ff5";
    public static final long TONY_STARK_ID = 1002L;
    private static final String MC_DUCK_EMAIL = "uncleduck@disney.com";
    private static final String MC_DUCK_NAME = "Uncle Scrooge Mc Duck";
    private static final String MC_DUCK_EXTERNAL_ID = "d83dae5a0c194ccb8ee4";
    public static final long MC_DUCK_ID = 1001L;
    private static final String BURNS_EMAIL = "montgomery@burns.com";
    private static final String BURNS_NAME = "Montgomery Burns";
    private static final String BURNS_EXTERNAL_ID = "f05fcg7c2e316eed0gg6";
    public static final long BURNS_ID = 1003L;
    private static final String X_RETRY_MESSAGE_HEADER = "x-retry";

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private EmailConfirmationRepository emailConfirmationRepository;

    @Test
    void personEventWhenConsumeWithSuccess() {
        var createPersonEvent = new CreatePersonEvent(
                TONY_STARK_ID, TONY_STARK_EXTERNAL_ID, TONY_STARK_EMAIL, TONY_STARK_NAME, PersonActionEnum.CREATED);

        rabbitTemplate.convertAndSend(PERSON_DIRECT_EXCHANGE.getProperty(),
                PERSON_CREATE_KEY.getProperty(), createPersonEvent);

        Awaitility.await()
                .atMost(Duration.ofSeconds(10))
                .pollInterval(Duration.ofSeconds(5))
                .until(() -> {
                    var emailConfirmation = emailConfirmationRepository.findConfirmationPending(TONY_STARK_ID);
                    return emailConfirmation.isPresent() &&
                            emailConfirmation.get().getEmailConfirmed().equals(YesNoEnum.NO) &&
                            emailConfirmation.get().getDateConfirmation() == null &&
                            emailConfirmation.get().getDateLimitConfirmation() != null &&
                            emailConfirmation.get().getToken() != null;
                });

    }

    @Test
    void personEventWhenPersonHasOldConfirmation() {
        var createPersonEvent = new CreatePersonEvent(
                BURNS_ID, BURNS_EXTERNAL_ID, BURNS_EMAIL, BURNS_NAME, PersonActionEnum.UPDATED);

        rabbitTemplate.convertAndSend(PERSON_DIRECT_EXCHANGE.getProperty(),
                PERSON_CREATE_KEY.getProperty(), createPersonEvent);

        Awaitility.await()
                .atMost(Duration.ofSeconds(10))
                .pollInterval(Duration.ofSeconds(5))
                .until(() -> {
                    var emailConfirmation = emailConfirmationRepository.findConfirmationPending(BURNS_ID);
                    return emailConfirmation.isPresent() &&
                            emailConfirmation.get().getEmailConfirmed().equals(YesNoEnum.NO) &&
                            emailConfirmation.get().getDateConfirmation() == null &&
                            emailConfirmation.get().getDateLimitConfirmation() != null &&
                            emailConfirmation.get().getToken() != null;
                });

    }

    @Test
    void personEventShouldRetry() {
        var createPersonEvent = new CreatePersonEvent(
                MC_DUCK_ID, MC_DUCK_EXTERNAL_ID, MC_DUCK_EMAIL, MC_DUCK_NAME, PersonActionEnum.CREATED);
        var expectedExpirationTime = String.valueOf(FIRST_RETRY.getExpirationTime());

        rabbitTemplate.convertAndSend(PERSON_DIRECT_EXCHANGE.getProperty(),
                PERSON_CREATE_KEY.getProperty(), createPersonEvent);

        Awaitility.await()
                .atMost(Duration.ofSeconds(30))
                .pollInterval(Duration.ofSeconds(5))
                .until(() -> {
                    var message = rabbitTemplate.receive(PersonQueueEnum.EMAIL_NOTIFICATION_PERSON_EVENT.getDelayedQueue());
                    return message != null &&
                            message.getMessageProperties() != null &&
                            message.getMessageProperties().getHeader(X_RETRY_MESSAGE_HEADER).equals(FIRST_RETRY.getRetryNumber()) &&
                            message.getMessageProperties().getExpiration().equals(expectedExpirationTime);
                });

    }

    @Test
    void personEventShouldRetryForTheLastTime() {
        var createPersonEvent = new CreatePersonEvent(
                MC_DUCK_ID, MC_DUCK_EXTERNAL_ID, MC_DUCK_EMAIL, MC_DUCK_NAME, PersonActionEnum.CREATED);
        var expectedExpirationTime = String.valueOf(THIRD_RETRY.getExpirationTime());

        rabbitTemplate.convertAndSend(PERSON_DIRECT_EXCHANGE.getProperty(),
                PERSON_CREATE_KEY.getProperty(), createPersonEvent, m -> {
                    m.getMessageProperties().setExpiration("20000");
                    m.getMessageProperties().setHeader("x-retry", "2");
                    return m;
                });

        Awaitility.await()
                .atMost(Duration.ofSeconds(10))
                .pollInterval(Duration.ofSeconds(5))
                .until(() -> {
                    var message = rabbitTemplate.receive(PersonQueueEnum.EMAIL_NOTIFICATION_PERSON_EVENT.getDelayedQueue());
                    return message != null &&
                            message.getMessageProperties() != null &&
                            message.getMessageProperties().getHeader(X_RETRY_MESSAGE_HEADER).equals(THIRD_RETRY.getRetryNumber()) &&
                            message.getMessageProperties().getExpiration().equals(expectedExpirationTime);
                });

    }

    @Test
    void personEventShouldSendMessageToDlq() {
        var createPersonEvent = new CreatePersonEvent(
                MC_DUCK_ID, MC_DUCK_EXTERNAL_ID, MC_DUCK_EMAIL, MC_DUCK_NAME, PersonActionEnum.CREATED);

        rabbitTemplate.convertAndSend(PERSON_DIRECT_EXCHANGE.getProperty(),
                PERSON_CREATE_KEY.getProperty(), createPersonEvent, m -> {
                    m.getMessageProperties().setExpiration("20000");
                    m.getMessageProperties().setHeader("x-retry", "3");
                    return m;
                });

        Awaitility.await()
                .atMost(Duration.ofSeconds(10))
                .pollInterval(Duration.ofSeconds(5))
                .until(() -> {
                    var delayedMessage = rabbitTemplate.receive(PersonQueueEnum.EMAIL_NOTIFICATION_PERSON_EVENT.getDelayedQueue());
                    var dlqMessage = rabbitTemplate.receive(PersonQueueEnum.EMAIL_NOTIFICATION_PERSON_EVENT.getDlqQueue());
                    return delayedMessage == null && dlqMessage != null;
                });

    }

}
