package com.tms.profilemanager.app.service.provider;

import com.tms.profilemanager.app.entity.ReceiptTemplate;
import com.tms.profilemanager.app.mapper.entity.util.OptionalMapper;
import com.tms.profilemanager.dto.create.ReceiptTemplateCreateDto;
import com.tms.profilemanager.dto.patch.ReceiptTemplatePatchDto;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", uses = OptionalMapper.class)
public abstract class ReceiptTemplateProvider {

    public abstract ReceiptTemplate toEntity(ReceiptTemplateCreateDto createDto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    public abstract void toEntity(ReceiptTemplatePatchDto patchDto, @MappingTarget ReceiptTemplate target);

}
