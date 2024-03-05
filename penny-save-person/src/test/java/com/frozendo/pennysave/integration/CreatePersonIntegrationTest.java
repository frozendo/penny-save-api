package com.frozendo.pennysave.integration;

import com.frozendo.pennysave.IntegrationTestsBase;
import com.frozendo.pennysave.controller.PersonController;
import com.frozendo.pennysave.domain.dto.request.PersonCreateRequest;
import com.frozendo.pennysave.enums.ApiMessageEnum;
import io.restassured.http.Method;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.time.LocalDate;

class CreatePersonIntegrationTest extends IntegrationTestsBase {

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
                "123@qwe#RS");

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
                "Bruce Wayne",
                LocalDate.now(),
                "123@qwe#RS");

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
        var personRequest = new PersonCreateRequest("bwayne@waynecorp.com",
                "Bruce Wayne",
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

}
