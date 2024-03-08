package com.frozendo.pennysave.integration;

import com.frozendo.pennysave.PersonModuleIntegrationTest;
import com.frozendo.pennysave.domain.dto.events.CreatePersonEvent;
import com.frozendo.pennysave.domain.enums.PersonActionEnum;
import com.frozendo.pennysave.domain.enums.PersonQueueEnum;
import com.frozendo.pennysave.enums.YesNoEnum;
import com.frozendo.pennysave.repository.EmailConfirmationRepository;
import com.icegreen.greenmail.configuration.GreenMailConfiguration;
import com.icegreen.greenmail.junit5.GreenMailExtension;
import com.icegreen.greenmail.util.ServerSetup;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;

import java.time.Duration;

import static com.frozendo.pennysave.domain.enums.PersonEventEnum.PERSON_CREATE_KEY;
import static com.frozendo.pennysave.domain.enums.PersonEventEnum.PERSON_DIRECT_EXCHANGE;
import static com.frozendo.pennysave.enums.RabbitRetryConfigEnum.FIRST_RETRY;
import static com.frozendo.pennysave.enums.RabbitRetryConfigEnum.SECOND_RETRY;
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

//    @RegisterExtension
//    protected static GreenMailExtension greenMail = new GreenMailExtension(getServerStartup())
//            .withConfiguration(GreenMailConfiguration.aConfig().withUser("penny-save-mail", "mailpassword"))
//            .withPerMethodLifecycle(true);

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private EmailConfirmationRepository emailConfirmationRepository;

    @Test
    void personEventWhenConsumeWithSuccess() {
        var createPersonEvent = new CreatePersonEvent(
                TONY_STARK_ID, TONY_STARK_EXTERNAL_ID, TONY_STARK_EMAIL, TONY_STARK_NAME, PersonActionEnum.CREATED);

        //startMailServerIfNecessary();

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

//    @Test
//    void personEventWhenPersonHasOldConfirmation() {
//        var createPersonEvent = new CreatePersonEvent(
//                BURNS_ID, BURNS_EXTERNAL_ID, BURNS_EMAIL, BURNS_NAME, PersonActionEnum.UPDATED);
//
//        //startMailServerIfNecessary();
//
//        rabbitTemplate.convertAndSend(PERSON_DIRECT_EXCHANGE.getProperty(),
//                PERSON_CREATE_KEY.getProperty(), createPersonEvent);
//
//        Awaitility.await()
//                .atMost(Duration.ofSeconds(10))
//                .pollInterval(Duration.ofSeconds(5))
//                .until(() -> {
//                    var emailConfirmation = emailConfirmationRepository.findConfirmationPending(BURNS_ID);
//                    return emailConfirmation.isPresent() &&
//                            emailConfirmation.get().getEmailConfirmed().equals(YesNoEnum.NO) &&
//                            emailConfirmation.get().getDateConfirmation() == null &&
//                            emailConfirmation.get().getDateLimitConfirmation() != null &&
//                            emailConfirmation.get().getToken() != null;
//                });
//
//    }
//
//    @Test
//    void personEventCheckEmailNotification() {
//        var createPersonEvent = new CreatePersonEvent(
//                TONY_STARK_ID, TONY_STARK_EXTERNAL_ID, TONY_STARK_EMAIL, TONY_STARK_NAME, PersonActionEnum.CREATED);
//
//       //startMailServerIfNecessary();
//
//        rabbitTemplate.convertAndSend(PERSON_DIRECT_EXCHANGE.getProperty(),
//                PERSON_CREATE_KEY.getProperty(), createPersonEvent);
//
//        Awaitility.await()
//                .atMost(Duration.ofSeconds(10))
//                .pollInterval(Duration.ofSeconds(5))
//                .until(() -> {
//                    var receivedMessage = greenMail.getReceivedMessages()[0];
//                    return receivedMessage != null &&
//                            receivedMessage.getSubject().equals("Penny Save - Confirme Sua Conta") &&
//                            receivedMessage.getAllRecipients()[0].toString().equals(TONY_STARK_EMAIL);
//                });
//
//    }
//
//    @Test
//    void personEventShouldRetry() {
//        var createPersonEvent = new CreatePersonEvent(
//                MC_DUCK_ID, MC_DUCK_EXTERNAL_ID, MC_DUCK_EMAIL, MC_DUCK_NAME, PersonActionEnum.CREATED);
//        var expectedExpirationTime = String.valueOf(FIRST_RETRY.getExpirationTime());
//
//       // stopMailServerIfNecessary();
//
//        rabbitTemplate.convertAndSend(PERSON_DIRECT_EXCHANGE.getProperty(),
//                PERSON_CREATE_KEY.getProperty(), createPersonEvent);
//
//        Awaitility.await()
//                .atMost(Duration.ofSeconds(10))
//                .pollInterval(Duration.ofSeconds(5))
//                .until(() -> {
//                    var message = rabbitTemplate.receive(PersonQueueEnum.EMAIL_NOTIFICATION_PERSON_EVENT.getDelayedQueue());
//                    return message != null &&
//                            message.getMessageProperties() != null &&
//                            message.getMessageProperties().getHeader(X_RETRY_MESSAGE_HEADER).equals(FIRST_RETRY.getRetryNumber()) &&
//                            message.getMessageProperties().getExpiration().equals(expectedExpirationTime);
//                });
//
//
//    }
//
//    @Test
//    void personEventShouldRetryForTheLastTime() {
//        var createPersonEvent = new CreatePersonEvent(
//                MC_DUCK_ID, MC_DUCK_EXTERNAL_ID, MC_DUCK_EMAIL, MC_DUCK_NAME, PersonActionEnum.CREATED);
//        var expectedExpirationTime = String.valueOf(THIRD_RETRY.getExpirationTime());
//
//        //stopMailServerIfNecessary();
//
//        rabbitTemplate.convertAndSend(PERSON_DIRECT_EXCHANGE.getProperty(),
//                PERSON_CREATE_KEY.getProperty(), createPersonEvent, m -> {
//                    m.getMessageProperties().setHeader(X_RETRY_MESSAGE_HEADER, SECOND_RETRY.getRetryNumber().toString());
//                    return m;
//                });
//
//        Awaitility.await()
//                .atMost(Duration.ofSeconds(10))
//                .pollInterval(Duration.ofSeconds(5))
//                .until(() -> {
//                    var message = rabbitTemplate.receive(PersonQueueEnum.EMAIL_NOTIFICATION_PERSON_EVENT.getDelayedQueue());
//                    return message != null &&
//                            message.getMessageProperties() != null &&
//                            message.getMessageProperties().getHeader(X_RETRY_MESSAGE_HEADER).equals(THIRD_RETRY.getRetryNumber()) &&
//                            message.getMessageProperties().getExpiration().equals(expectedExpirationTime);
//                });
//
//    }
//
//    @Test
//    void personEventShouldSendMessageToDlq() {
//        var createPersonEvent = new CreatePersonEvent(
//                MC_DUCK_ID, MC_DUCK_EXTERNAL_ID, MC_DUCK_EMAIL, MC_DUCK_NAME, PersonActionEnum.CREATED);
//
//        //stopMailServerIfNecessary();
//
//        rabbitTemplate.convertAndSend(PERSON_DIRECT_EXCHANGE.getProperty(),
//                PERSON_CREATE_KEY.getProperty(), createPersonEvent, m -> {
//                    m.getMessageProperties().setHeader(X_RETRY_MESSAGE_HEADER, THIRD_RETRY.getRetryNumber().toString());
//                    return m;
//                });
//
//        Awaitility.await()
//                .atMost(Duration.ofSeconds(10))
//                .pollInterval(Duration.ofSeconds(5))
//                .until(() -> {
//                    var delayedMessage = rabbitTemplate.receive(PersonQueueEnum.EMAIL_NOTIFICATION_PERSON_EVENT.getDelayedQueue());
//                    var dlqMessage = rabbitTemplate.receive(PersonQueueEnum.EMAIL_NOTIFICATION_PERSON_EVENT.getDlqQueue());
//                    return delayedMessage == null && dlqMessage != null;
//                });
//
//    }

//    private void stopMailServerIfNecessary() {
//        if (greenMail.isRunning()) {
//            greenMail.stop();
//        }
//    }
//
//    private void startMailServerIfNecessary() {
//        if (!greenMail.isRunning()) {
//            greenMail.start();
//        }
//    }

    private static ServerSetup getServerStartup() {
        final ServerSetup serverSetup = new ServerSetup(3025, null, ServerSetup.PROTOCOL_SMTP);
        serverSetup.setServerStartupTimeout(5000L);
        return serverSetup;
    }

}
