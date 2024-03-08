package com.pennysave.frozendo;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pennysave.frozendo.config.DockerTestEnvironment;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.server.LocalServerPort;

public class IntegrationTestsBase {

    @LocalServerPort
    private Integer port;

    @Autowired
    private ObjectMapper mapper;

    protected RequestSpecification getRequest() {
        var spec = new RequestSpecBuilder()
                .setContentType(ContentType.JSON)
                .build();
        return RestAssured.given()
                .when()
                .spec(spec)
                .log().all()
                .port(port);
    }

    protected String getJson(Object object) {
        try {
            return mapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            return StringUtils.EMPTY;
        }
    }

}
