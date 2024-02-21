package com.frozendo.pennysave;

import com.frozendo.pennysave.config.DockerTestEnvironment;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

@SpringBootConfiguration
@EnableAutoConfiguration
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class IntegrationTestsBase extends DockerTestEnvironment {

    @LocalServerPort
    private Integer port;

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

}
