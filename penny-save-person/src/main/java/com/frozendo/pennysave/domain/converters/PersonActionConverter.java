package com.frozendo.pennysave.domain.converters;

import com.frozendo.pennysave.domain.enums.PersonActionEnum;
import jakarta.persistence.AttributeConverter;

public class PersonActionConverter implements AttributeConverter<PersonActionEnum, Character> {

    @Override
    public Character convertToDatabaseColumn(PersonActionEnum personAction) {
        return PersonActionEnum.convertToPersonActionCode(personAction);
    }

    @Override
    public PersonActionEnum convertToEntityAttribute(Character character) {
        return PersonActionEnum.convertToPersonActionEnum(character);
    }
}
