package com.frozendo.pennysave.domain.dto.response;

import com.frozendo.pennysave.domain.entity.Person;

import java.time.LocalDate;

public record PersonResponse(
        String id,
        String name,
        String email,
        LocalDate birthDate) {

    public PersonResponse(Person person) {
        this(person.getExternalId(), person.getName(), person.getEmail(), person.getBirthDate());
    }

}
