package com.tms.profilemanager.app.service.provider.modificatiion;

import com.tms.profilemanager.app.entity.modification.ModificationContactlessApp;
import com.tms.profilemanager.app.mapper.entity.util.EnumFieldModificationMapper;
import com.tms.profilemanager.app.mapper.entity.util.OptionalMapper;
import com.tms.profilemanager.app.mapper.entity.util.SetModificationMapper;
import com.tms.profilemanager.app.service.ContactlessAppService;
import com.tms.profilemanager.app.service.ModificationPaymentSystemService;
import com.tms.profilemanager.app.service.provider.EntityProvider;
import com.tms.profilemanager.dto.create.modification.ModificationContactlessAppCreateDto;
import com.tms.profilemanager.dto.patch.modification.ModificationContactlessAppPatchDto;
import org.mapstruct.AfterMapping;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;

@Mapper(componentModel = "spring", uses = {OptionalMapper.class, SetModificationMapper.class, EnumFieldModificationMapper.class})
public abstract class ModificationContactlessAppProvider extends EntityProvider<ModificationContactlessApp, ModificationContactlessAppCreateDto, ModificationContactlessAppPatchDto> {

    @Autowired
    @Lazy
    ModificationPaymentSystemService modificationPaymentSystemService;
    @Autowired
    @Lazy
    ContactlessAppService contactlessAppService;

    @Override
    public abstract void initializeEntity(ModificationContactlessAppCreateDto source, @MappingTarget ModificationContactlessApp target);

    @AfterMapping
    protected void afterInitializeEntity(ModificationContactlessAppCreateDto source, @MappingTarget ModificationContactlessApp target) {
        if(source.getPaymentSystemId() != null && source.getGroupId() != null) {
            target.setModificationContactlessAppsPaymentSystem(modificationPaymentSystemService.findOrCreateModificationPaymentSystem(source.getPaymentSystemId(), source.getGroupId()));
        }
        contactlessAppService.existsByIdOrElseThrow(source.getContactlessAppsId());
        target.setContactlessAppId(source.getContactlessAppsId());
    }

    @Override
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    public abstract void patchEntity(ModificationContactlessAppPatchDto source, @MappingTarget ModificationContactlessApp target);

    @AfterMapping
    protected void afterPatchEntity(ModificationContactlessAppPatchDto source, @MappingTarget ModificationContactlessApp target) {
        if (source.getContactlessAppsId() != null) {
            contactlessAppService.existsByIdOrElseThrow(source.getContactlessAppsId());
            target.setContactlessAppId(source.getContactlessAppsId());
        }
        if (source.getPaymentSystemId() != null && source.getGroupId() != null) {
            target.setModificationContactlessAppsPaymentSystem(modificationPaymentSystemService.findOrCreateModificationPaymentSystem(source.getPaymentSystemId(), source.getGroupId()));
        }
    }

    @Override
    protected Class<ModificationContactlessApp> getEntityClass() {
        return ModificationContactlessApp.class;
    }

}
