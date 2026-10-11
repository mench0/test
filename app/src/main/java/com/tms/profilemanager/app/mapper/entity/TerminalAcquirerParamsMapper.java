package com.tms.profilemanager.app.mapper.entity;

import com.tms.profilemanager.app.entity.TerminalAcquirerParams;
import com.tms.profilemanager.app.service.AcquirerService;
import com.tms.profilemanager.dto.TerminalAcquirerParamsDto;
import com.tms.profilemanager.dto.TerminalAcquirerParamsDto.TerminalAcquirerParamsDtoBuilder;
import com.tms.profilemanager.dto.enums.TerminalType;
import com.tms.profilemanager.dto.view.TerminalAcquirerParamsView;
import com.tms.profilemanager.dto.view.TerminalAcquirerParamsView.TerminalAcquirerParamsViewBuilder;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(componentModel = "spring", uses = AcquirerMapper.class)
public abstract class TerminalAcquirerParamsMapper implements EntityMapper<TerminalAcquirerParams, TerminalAcquirerParamsDto> {

    @Autowired AcquirerMapper acquirerMapper;
    @Autowired AcquirerService acquirerService;
    @Autowired PaymentTypeMapper paymentTypeMapper;

    @Mapping(target = "type", ignore = true)
    @Mapping(target = "availableOperations", ignore = true)
    public abstract TerminalAcquirerParamsDto toDto(TerminalAcquirerParams entity);

    @AfterMapping
    protected void afterToDto(@MappingTarget TerminalAcquirerParamsDtoBuilder<?, ?> dto, TerminalAcquirerParams entity) {
        var acquirer = acquirerService.getByIdUsingCacheWithoutAccessControl(entity.getAcquirerId()).orElseThrow();
        dto.acquirerId(entity.getAcquirerId());
        dto.acquirerName(acquirer.getName());
        dto.type(TerminalType.Dto.of(entity.getType()));
        dto.availableOperations(entity.getOrderedAvailableOperations());
    }

    @Mapping(target = "acquirer", ignore = true)
    @Mapping(target = "availableOperations", ignore = true)
    @Mapping(target = "kcvUpdatedDate", ignore = true)
    @Mapping(target = "paymentTypes", ignore = true)
    public abstract TerminalAcquirerParamsView toView(TerminalAcquirerParams entity);

    @AfterMapping
    public void afterToView(@MappingTarget TerminalAcquirerParamsViewBuilder<?, ?> dto, TerminalAcquirerParams entity) {
        var acquirer = acquirerService.getByIdUsingCacheWithoutAccessControl(entity.getAcquirerId()).orElseThrow();
        dto.acquirer(acquirerMapper.toView(acquirer, entity.getType()));
        dto.availableOperations(entity.getOrderedAvailableOperations());
        dto.paymentTypes(paymentTypeMapper.paymentTypeToDto(entity.getParamsPaymentTypes()));
    }
}
