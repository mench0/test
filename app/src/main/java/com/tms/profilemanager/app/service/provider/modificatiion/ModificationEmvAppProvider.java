package com.tms.profilemanager.app.service.provider.modificatiion;

import com.tms.profilemanager.app.entity.modification.ModificationEmvApp;
import com.tms.profilemanager.app.mapper.entity.util.EnumFieldModificationMapper;
import com.tms.profilemanager.app.mapper.entity.util.OptionalMapper;
import com.tms.profilemanager.app.service.EMVAppService;
import com.tms.profilemanager.app.service.ModificationPaymentSystemService;
import com.tms.profilemanager.app.service.provider.EntityProvider;
import com.tms.profilemanager.dto.create.modification.ModificationEmvAppCreateDto;
import com.tms.profilemanager.dto.patch.modification.ModificationEmvAppPatchDto;
import org.mapstruct.AfterMapping;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;

@Mapper(componentModel = "spring", uses = {OptionalMapper.class, EnumFieldModificationMapper.class})
public abstract class ModificationEmvAppProvider extends EntityProvider<ModificationEmvApp, ModificationEmvAppCreateDto, ModificationEmvAppPatchDto> {

    @Autowired
    @Lazy
    ModificationPaymentSystemService modificationPaymentSystemService;
    @Autowired
    @Lazy
    EMVAppService emvAppService;

    @Override
    public abstract void initializeEntity(ModificationEmvAppCreateDto source, @MappingTarget ModificationEmvApp target);

    @AfterMapping
    protected void afterInitializeEntity(ModificationEmvAppCreateDto source, @MappingTarget ModificationEmvApp target) {
        if(source.getPaymentSystemId() != null && source.getGroupId() != null) {
            target.setModificationEmvAppsPaymentSystem(modificationPaymentSystemService.findOrCreateModificationPaymentSystem(source.getPaymentSystemId(), source.getGroupId()));
        }
        emvAppService.existsByIdOrElseThrow(source.getEmvAppsId());
        target.setEmvAppId(source.getEmvAppsId());
    }

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    public abstract void patchEntity(ModificationEmvAppPatchDto source, @MappingTarget ModificationEmvApp target);

    @AfterMapping
    protected void afterPatchEntity(ModificationEmvAppPatchDto source, @MappingTarget ModificationEmvApp target) {
        if(source.getPaymentSystemId() != null && source.getGroupId() != null) {
            target.setModificationEmvAppsPaymentSystem(modificationPaymentSystemService.findOrCreateModificationPaymentSystem(source.getPaymentSystemId(), source.getGroupId()));        }
        if (source.getEmvAppsId() != null) {
            emvAppService.existsByIdOrElseThrow(source.getEmvAppsId());
            target.setEmvAppId(source.getEmvAppsId());
        }
    }

    @Override
    protected Class<ModificationEmvApp> getEntityClass() {
        return ModificationEmvApp.class;
    }
}
