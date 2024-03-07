package com.frozendo.pennysave.domain.dto.events;

import com.frozendo.pennysave.domain.entity.Person;

public record CreatePersonEvent(
        Long id,
        String externalId,
        String email,
        String name
) {

    public CreatePersonEvent(Person person) {
        this(person.getId(), person.getExternalId(), person.getEmail(), person.getName());
    }

}
