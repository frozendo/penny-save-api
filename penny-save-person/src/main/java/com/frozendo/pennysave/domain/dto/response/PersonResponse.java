package com.frozendo.pennysave.domain.dto.response;

import com.frozendo.pennysave.domain.entity.Person;
import com.frozendo.pennysave.domain.enums.StatusPersonEnum;

import java.time.LocalDate;

public record PersonResponse(
        String id,
        String name,
        String email,
        LocalDate birthDate,
        StatusPersonEnum status) {

    public PersonResponse(Person person) {
        this(
                person.getExternalId(),
                person.getName(),
                person.getEmail(),
                person.getBirthDate(),
                person.getStatus()
        );
    }

}
