package com.tms.profilemanager.app.mapper.entity;

import com.tms.profilemanager.app.entity.ContactlessLimit;
import com.tms.profilemanager.dto.ContactlessLimitDto;
import com.tms.profilemanager.dto.CurrencyDto;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;


@FieldDefaults(level = AccessLevel.PRIVATE)
@Mapper(componentModel = "spring", uses = DRLMapper.class)
public abstract class ContactlessLimitMapper implements EntityMapper<ContactlessLimit, ContactlessLimitDto> {

    @AfterMapping
    protected void afterToDto(@MappingTarget ContactlessLimitDto.ContactlessLimitDtoBuilder<?, ?> dto, ContactlessLimit entity) {
        dto.currency(CurrencyDto.of(entity.getCurrency()));
    }
}
