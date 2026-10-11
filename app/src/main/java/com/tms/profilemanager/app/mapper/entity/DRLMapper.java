package com.tms.profilemanager.app.mapper.entity;

import com.tms.profilemanager.app.entity.DRL;
import com.tms.profilemanager.dto.DRLDto;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Mapper(componentModel = "spring")
public abstract class DRLMapper implements EntityMapper<DRL, DRLDto> {

    @Override
    @Mapping(target = "contactlessLimitId", source = "contactlessLimit.id")
    public abstract DRLDto toDto(DRL entity);
}
