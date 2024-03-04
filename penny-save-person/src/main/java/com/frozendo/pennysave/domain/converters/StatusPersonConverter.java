package com.frozendo.pennysave.domain.converters;

import com.frozendo.pennysave.domain.enums.StatusPersonEnum;
import jakarta.persistence.AttributeConverter;

public class StatusPersonConverter implements AttributeConverter<StatusPersonEnum, Character> {

    @Override
    public Character convertToDatabaseColumn(StatusPersonEnum statusPerson) {
        return StatusPersonEnum.convertToStatusCode(statusPerson);
    }

    @Override
    public StatusPersonEnum convertToEntityAttribute(Character character) {
        return StatusPersonEnum.convertToStatusEnum(character);
    }
}
