package com.frozendo.pennysave.domain.converters;

import com.frozendo.pennysave.domain.enums.PersonOperationEnum;
import jakarta.persistence.AttributeConverter;

public class PersonOperationConverter implements AttributeConverter<PersonOperationEnum, Character> {

    @Override
    public Character convertToDatabaseColumn(PersonOperationEnum personOperation) {
        return PersonOperationEnum.convertToPersonOperationCode(personOperation);
    }

    @Override
    public PersonOperationEnum convertToEntityAttribute(Character character) {
        return PersonOperationEnum.convertToPersonOperationEnum(character);
    }
}
