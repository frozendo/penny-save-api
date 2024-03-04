package com.frozendo.pennysave.converters;

import com.frozendo.pennysave.enums.YesNoEnum;
import jakarta.persistence.AttributeConverter;

public class YesNoConverter implements AttributeConverter<YesNoEnum, Character> {

    @Override
    public Character convertToDatabaseColumn(YesNoEnum statusPerson) {
        return YesNoEnum.convertEnumToCode(statusPerson);
    }

    @Override
    public YesNoEnum convertToEntityAttribute(Character character) {
        return YesNoEnum.convertCodeToEnum(character);
    }
}
