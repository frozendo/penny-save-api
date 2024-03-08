package com.frozendo.pennysave.domain.dto.events;

import com.frozendo.pennysave.domain.entity.Person;
import com.frozendo.pennysave.domain.enums.PersonActionEnum;

public record CreatePersonEvent(
        Long id,
        String externalId,
        String email,
        String name,
        PersonActionEnum action
) {

    public CreatePersonEvent(Person person, PersonActionEnum action) {
        this(
                person.getId(),
                person.getExternalId(),
                person.getEmail(),
                person.getName(),
                action
        );
    }

}
