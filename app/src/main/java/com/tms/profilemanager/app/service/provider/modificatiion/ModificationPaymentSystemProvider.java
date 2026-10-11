package com.tms.profilemanager.app.service.provider.modificatiion;

import com.tms.profilemanager.app.entity.BaseEntity;
import com.tms.profilemanager.app.entity.modification.ModificationPaymentSystem;
import com.tms.profilemanager.app.mapper.entity.util.OptionalMapper;
import com.tms.profilemanager.app.service.GeneralLimitService;
import com.tms.profilemanager.app.service.PaymentSystemService;
import com.tms.profilemanager.app.service.TerminalGroupService;
import com.tms.profilemanager.app.service.provider.EntityProvider;
import com.tms.profilemanager.dto.create.modification.ModificationPaymentSystemCreateDto;
import com.tms.profilemanager.dto.patch.modification.ModificationPaymentSystemPatchDto;
import org.mapstruct.AfterMapping;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;

import java.util.stream.Collectors;

@Mapper(componentModel = "spring", uses = OptionalMapper.class)
public abstract class ModificationPaymentSystemProvider extends EntityProvider<ModificationPaymentSystem, ModificationPaymentSystemCreateDto, ModificationPaymentSystemPatchDto> {

    @Autowired
    @Lazy
    TerminalGroupService terminalGroupService;
    @Autowired @Lazy
    PaymentSystemService paymentSystemService;
    @Autowired @Lazy
    GeneralLimitService generalLimitService;

    @Override
    public abstract void initializeEntity(ModificationPaymentSystemCreateDto source, @MappingTarget ModificationPaymentSystem target);

    @AfterMapping
    protected void afterInitializeEntity(ModificationPaymentSystemCreateDto source, @MappingTarget ModificationPaymentSystem target) {
        paymentSystemService.existsByIdOrElseThrow(source.getPaymentSystemId());
        target.setPaymentSystemId(source.getPaymentSystemId());
        terminalGroupService.existsByIdOrElseThrow(source.getGroupId());
        target.setGroupId(source.getGroupId());
        target.setLimitIds(generalLimitService.getAllByIdsLimitsUsingCacheWithoutAccessControl(source.getLimits()).stream()
                .map(BaseEntity::getId)
                .collect(Collectors.toSet()));
    }

    @Override
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    public abstract void patchEntity(ModificationPaymentSystemPatchDto source, @MappingTarget ModificationPaymentSystem target);

    @AfterMapping
    public void afterPatchEntity(ModificationPaymentSystemPatchDto source, @MappingTarget ModificationPaymentSystem target) {
        paymentSystemService.existsByIdOrElseThrow(source.getPaymentSystemId());
        target.setPaymentSystemId(source.getPaymentSystemId());
        target.setLimitIds(generalLimitService.getAllByIdsLimitsUsingCacheWithoutAccessControl(source.getLimits()).stream()
                .map(BaseEntity::getId)
                .collect(Collectors.toSet()));
    }

    @Override
    protected Class<ModificationPaymentSystem> getEntityClass() {
        return ModificationPaymentSystem.class;
    }

}
