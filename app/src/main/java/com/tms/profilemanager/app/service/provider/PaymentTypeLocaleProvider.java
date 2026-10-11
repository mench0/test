package com.tms.profilemanager.app.service.provider;

import com.tms.profilemanager.app.entity.PaymentTypeLocale;
import com.tms.profilemanager.app.mapper.entity.util.OptionalMapper;
import com.tms.profilemanager.dto.create.PaymentTypeCreateDto.PaymentTypeLocaleInputDto;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring", uses = OptionalMapper.class)
public abstract class PaymentTypeLocaleProvider {

    protected abstract PaymentTypeLocale create(PaymentTypeLocaleInputDto inputDto);

    protected abstract void patch(PaymentTypeLocaleInputDto inputDto, @MappingTarget PaymentTypeLocale entity);

}
