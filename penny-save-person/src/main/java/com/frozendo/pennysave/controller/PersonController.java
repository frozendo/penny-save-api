package com.frozendo.pennysave.controller;

import com.frozendo.pennysave.domain.dto.request.PersonCreateRequest;
import com.frozendo.pennysave.domain.dto.response.PersonResponse;
import com.frozendo.pennysave.domain.entity.Person;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(PersonController.URI)
public class PersonController {
    public static final String URI = "/people";

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PersonResponse createPerson(@Valid @RequestBody PersonCreateRequest personRequest) {
        return new PersonResponse(new Person());
    }

}