package com.frozendo.pennysave.integration;

import com.frozendo.pennysave.IntegrationTestsBase;
import com.frozendo.pennysave.controller.PersonController;
import com.frozendo.pennysave.domain.dto.request.PersonCreateRequest;
import com.frozendo.pennysave.domain.enums.PersonMessageEnum;
import com.frozendo.pennysave.domain.enums.StatusPersonEnum;
import com.frozendo.pennysave.enums.ApiMessageEnum;
import com.frozendo.pennysave.repository.PersonRepository;
import io.restassured.http.Method;
import org.assertj.core.api.Assertions;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.jdbc.Sql;

import java.time.LocalDate;

@Sql(value = {"/scripts/person.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(value = {"/scripts/clean.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
class CreatePersonIntegrationTest extends IntegrationTestsBase {

    private static final String PERSON_EMAIL = "bwayne@waynecorp.com";
    private static final String PERSON_NAME = "Bruce Wayne";
    private static final String PERSON_PASSWORD = "123@qwe#RS";

    @Autowired
    private PersonRepository personRepository;

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
    void createNewPersonSuccessful() {
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
    }

    @Test
    void createNewPersonWhenEmailIsDuplicated() {
        var personRequest = new PersonCreateRequest("uncleduck@disney.com",
                PERSON_NAME,
                LocalDate.now(),
                PERSON_PASSWORD);

        getRequest()
                .body(getJson(personRequest))
                .request(Method.POST, PersonController.URI)
                .then()
                .log().all()
                .statusCode(HttpStatus.UNPROCESSABLE_ENTITY.value())
                .body("code", Matchers.equalTo(PersonMessageEnum.EMAIL_DUPLICATED.getCode()))
                .body("message", Matchers.equalTo(PersonMessageEnum.EMAIL_DUPLICATED.getMessage()));
    }

}
