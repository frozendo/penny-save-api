package com.frozendo.pennysave.domain.dto.events;

import com.frozendo.pennysave.domain.entity.Person;
import com.frozendo.pennysave.domain.enums.PersonOperationEnum;

public record CreatePersonEvent(
        Long id,
        String externalId,
        String email,
        String name,
        PersonOperationEnum operation
) {

    public CreatePersonEvent(Person person, PersonOperationEnum operation) {
        this(
                person.getId(),
                person.getExternalId(),
                person.getEmail(),
                person.getName(),
                operation
        );
    }

}
