package com.tms.profilemanager.app.mapper.entity.util;

import com.tms.profilemanager.app.entity.modification.SetModification;
import com.tms.profilemanager.dto.modification.SetModificationDto;
import org.mapstruct.Mapper;

import static java.util.Optional.ofNullable;

@Mapper(componentModel = "spring")
public class SetModificationMapper {

    public <T> SetModification<T> map(SetModificationDto<T> source) {
        return SetModification.<T>builder()
                .value(ofNullable(source).map(SetModificationDto::getValue).orElse(null))
                .ignored(ofNullable(source).map(SetModificationDto::isIgnored).orElse(true))
                .build();
    }
}
