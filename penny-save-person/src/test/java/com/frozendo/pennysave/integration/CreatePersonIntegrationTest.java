package com.frozendo.pennysave.integration;

import com.frozendo.pennysave.PersonModuleIntegrationTest;
import com.frozendo.pennysave.controller.PersonController;
import com.frozendo.pennysave.domain.dto.events.CreatePersonEvent;
import com.frozendo.pennysave.domain.dto.request.PersonCreateRequest;
import com.frozendo.pennysave.domain.enums.PersonBusinessMessageEnum;
import com.frozendo.pennysave.domain.enums.PersonActionEnum;
import com.frozendo.pennysave.domain.enums.StatusPersonEnum;
import com.frozendo.pennysave.enums.ApiMessageEnum;
import com.frozendo.pennysave.repository.PersonRepository;
import io.restassured.http.Method;
import org.awaitility.Awaitility;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.jdbc.Sql;

import java.time.Duration;
import java.time.LocalDate;

import static com.frozendo.pennysave.domain.enums.PersonEventEnum.PERSON_CREATE_KEY;
import static com.frozendo.pennysave.domain.enums.PersonEventEnum.PERSON_DIRECT_EXCHANGE;
import static org.assertj.core.api.Assertions.assertThat;

@Sql(value = {"/scripts/person.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(value = {"/scripts/clean.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
class CreatePersonIntegrationTest extends PersonModuleIntegrationTest {

    private static final String PERSON_EMAIL = "bwayne@waynecorp.com";
    private static final String PERSON_NAME = "Bruce Wayne";
    private static final String PERSON_PASSWORD = "123@qwe#RS";
    private static final String TEST_QUEUE = "test-queue";

    @Autowired
    private PersonRepository personRepository;

    @Autowired
    private BCryptPasswordEncoder encoder;

    @Autowired
    private RabbitAdmin rabbitAdmin;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Test
    void createNewPersonWithoutMandatoryFields() {
        var personRequest = new PersonCreateRequest("", "", null, "");

        getRequest()
                .body(getJson(personRequest))
                .request(Method.POST, PersonController.URI)
                .then()
                .log().all()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .body("size()", Matchers.is(4));
    }

    @Test
    void createNewPersonWhenFieldsAreLargerThanAllowed() {
        var personRequest = new PersonCreateRequest("testwithlargeemailtohappenanerror@tester.domain.com",
                "Large Name bigger than the maximum of 80 characters to test this field on our api",
                LocalDate.now(),
                PERSON_PASSWORD);

        getRequest()
                .body(getJson(personRequest))
                .request(Method.POST, PersonController.URI)
                .then()
                .log().all()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .body("size()", Matchers.is(2));
    }

    @Test
    void createNewPersonWhenEmailIsInvalid() {
        var personRequest = new PersonCreateRequest("invalidemail",
                PERSON_NAME,
                LocalDate.now(),
                PERSON_PASSWORD);

        getRequest()
                .body(getJson(personRequest))
                .request(Method.POST, PersonController.URI)
                .then()
                .log().all()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .body("size()", Matchers.is(1))
                .body("[0].message", Matchers.is(ApiMessageEnum.PERSON_EMAIL_INVALID.getMessage()));
    }

    @Test
    void createNewPersonWhenPasswordPasswordDoesNotMeetTheRequirements() {
        var personRequest = new PersonCreateRequest(PERSON_EMAIL,
                PERSON_NAME,
                LocalDate.now(),
                "abc");

        getRequest()
                .body(getJson(personRequest))
                .request(Method.POST, PersonController.URI)
                .then()
                .log().all()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .body("size()", Matchers.is(1))
                .body("[0].message", Matchers.is(ApiMessageEnum.PERSON_PASSWORD_INVALID.getMessage()));
    }

    @Test
    void createNewPersonSuccessfulAndCheckPasswordEncryption() {
        var personRequest = new PersonCreateRequest(PERSON_EMAIL,
                PERSON_NAME,
                LocalDate.now(),
                PERSON_PASSWORD);

        getRequest()
                .body(getJson(personRequest))
                .request(Method.POST, PersonController.URI)
                .then()
                .log().all()
                .statusCode(HttpStatus.CREATED.value())
                .body("id", Matchers.notNullValue())
                .body("status", Matchers.equalTo(StatusPersonEnum.PENDING.name()));

        var optionalPerson = personRepository.findByEmail(PERSON_EMAIL);
        assertThat(optionalPerson).isPresent();

        var savedPerson = optionalPerson.get();
        assertThat(savedPerson.getPassword()).isNotEmpty();
        assertThat(savedPerson.getPassword()).isNotEqualTo(PERSON_PASSWORD);

        var result = encoder.matches(PERSON_PASSWORD, savedPerson.getPassword());

        assertThat(result).isTrue();
    }

    @Test
    void createNewPersonSuccessfulAndCheckEventTriggered() {
        createQueueAndSubscribeForPersonCreateEvent();

        var personRequest = new PersonCreateRequest(PERSON_EMAIL,
                PERSON_NAME,
                LocalDate.now(),
                PERSON_PASSWORD);

        var personId = getRequest()
                .body(getJson(personRequest))
                .request(Method.POST, PersonController.URI)
                .then()
                .log().all()
                .statusCode(HttpStatus.CREATED.value())
                .body("id", Matchers.notNullValue())
                .body("status", Matchers.equalTo(StatusPersonEnum.PENDING.name()))
                .extract()
                .path("id");

        Awaitility.await()
                .atMost(Duration.ofSeconds(10))
                .pollInterval(Duration.ofSeconds(5))
                .until(() -> {
                        var personEvent = (CreatePersonEvent) rabbitTemplate.receiveAndConvert(TEST_QUEUE);
                        return personEvent != null &&
                                personEvent.externalId().equals(personId) &&
                                personEvent.name().equals(PERSON_NAME) &&
                                personEvent.email().equals(PERSON_EMAIL) &&
                                personEvent.action().equals(PersonActionEnum.CREATED);
                });
    }

    @Test
    void createNewPersonWhenEmailIsDuplicated() {
        var personRequest = new PersonCreateRequest("montgomery@burns.com",
                PERSON_NAME,
                LocalDate.now(),
                PERSON_PASSWORD);

        getRequest()
                .body(getJson(personRequest))
                .request(Method.POST, PersonController.URI)
                .then()
                .log().all()
                .statusCode(HttpStatus.UNPROCESSABLE_ENTITY.value())
                .body("code", Matchers.equalTo(PersonBusinessMessageEnum.EMAIL_DUPLICATED.getCode()))
                .body("message", Matchers.equalTo(PersonBusinessMessageEnum.EMAIL_DUPLICATED.getMessage()));
    }

    private void createQueueAndSubscribeForPersonCreateEvent() {
        var queue = QueueBuilder
                .durable(TEST_QUEUE)
                .build();
        rabbitAdmin.declareQueue(queue);

        var binding = new Binding(TEST_QUEUE,
                Binding.DestinationType.QUEUE, PERSON_DIRECT_EXCHANGE.getProperty(),
                PERSON_CREATE_KEY.getProperty(), null);
        rabbitAdmin.declareBinding(binding);
    }

}
