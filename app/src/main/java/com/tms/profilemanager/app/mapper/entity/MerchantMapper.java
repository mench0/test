package com.tms.profilemanager.app.mapper.entity;

import com.tms.profilemanager.app.entity.Merchant;
import com.tms.profilemanager.dto.MerchantDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public abstract class MerchantMapper implements EntityMapper<Merchant, MerchantDto> {
}
