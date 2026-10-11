package com.tms.profilemanager.app.mapper.entity;

import com.tms.profilemanager.app.entity.ContactLimit;
import com.tms.profilemanager.dto.ContactLimitDto;
import com.tms.profilemanager.dto.CurrencyDto;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public abstract class ContactLimitMapper implements EntityMapper<ContactLimit, ContactLimitDto> {

    @AfterMapping
    protected void afterToDto(@MappingTarget ContactLimitDto.ContactLimitDtoBuilder<?, ?> dto, ContactLimit entity) {
        dto.currency(CurrencyDto.of(entity.getCurrency()));
    }
}
