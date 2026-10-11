package com.tms.profilemanager.app.mapper.entity.util;

import com.tms.profilemanager.app.entity.modification.EnumFieldModification;
import com.tms.profilemanager.dto.modification.FieldModificationDto;
import org.mapstruct.Mapper;

import static java.util.Optional.ofNullable;

@Mapper(componentModel = "spring")
public class EnumFieldModificationMapper {

    public <T> EnumFieldModification<T> map(FieldModificationDto<T> source) {
        return EnumFieldModification.<T>builder()
                .value(ofNullable(source).map(FieldModificationDto::getValue).orElse(null))
                .ignored(ofNullable(source).map(FieldModificationDto::isIgnored).orElse(true))
                .build();
    }
}
