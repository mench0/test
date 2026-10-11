package com.tms.profilemanager.app.service.provider;

import com.tms.profilemanager.app.entity.PaymentSystem;
import com.tms.profilemanager.app.mapper.entity.util.OptionalMapper;
import com.tms.profilemanager.app.service.ContactLimitService;
import com.tms.profilemanager.app.service.ContactlessLimitService;
import com.tms.profilemanager.app.service.GeneralLimitService;
import com.tms.profilemanager.dto.create.PaymentSystemCreateDto;
import com.tms.profilemanager.dto.patch.PaymentSystemPatchDto;
import org.mapstruct.AfterMapping;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;

import java.util.HashSet;

@Mapper(componentModel = "spring", uses = OptionalMapper.class)
public abstract class PaymentSystemProvider extends EntityProvider<PaymentSystem, PaymentSystemCreateDto, PaymentSystemPatchDto> {

    @Autowired
    @Lazy
    ContactlessLimitService contactlessLimitService;
    @Autowired
    @Lazy
    ContactLimitService contactLimitService;
    @Autowired
    @Lazy
    GeneralLimitService generalLimitService;

    @Mapping(target = "limits", ignore = true)
    protected abstract void initializeEntity(PaymentSystemCreateDto source, @MappingTarget PaymentSystem target);

    @AfterMapping
    protected void afterInitializeEntity(PaymentSystemCreateDto source, @MappingTarget PaymentSystem target) {
        if (source.getLimits() != null) {
            target.setLimits(new HashSet<>(generalLimitService.getByIds(source.getLimits())));
        }
    }

    @Override
    @Mapping(target = "limits", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    public abstract void patchEntity(PaymentSystemPatchDto source, @MappingTarget PaymentSystem target);

    @AfterMapping
    protected void afterPatchEntity(PaymentSystemPatchDto source, @MappingTarget PaymentSystem target) {
        target.setLimits(new HashSet<>(generalLimitService.getByIds(source.getLimits())));
    }

    @Override
    protected Class<PaymentSystem> getEntityClass() {
        return PaymentSystem.class;
    }
}
