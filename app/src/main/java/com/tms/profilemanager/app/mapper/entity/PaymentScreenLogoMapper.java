package com.tms.profilemanager.app.mapper.entity;

import com.tms.profilemanager.app.entity.PaymentScreenLogo;
import com.tms.profilemanager.app.mapper.entity.util.OptionalMapper;
import com.tms.profilemanager.dto.PaymentScreenLogoDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = OptionalMapper.class)
public abstract class PaymentScreenLogoMapper implements EntityMapper<PaymentScreenLogo, PaymentScreenLogoDto> {

    @Override
    public abstract PaymentScreenLogoDto toDto(PaymentScreenLogo entity);

}
