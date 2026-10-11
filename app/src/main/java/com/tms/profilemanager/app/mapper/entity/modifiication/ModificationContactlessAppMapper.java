package com.tms.profilemanager.app.mapper.entity.modifiication;

import com.tms.profilemanager.app.entity.modification.ModificationContactlessApp;
import com.tms.profilemanager.app.mapper.entity.EntityMapper;
import com.tms.profilemanager.app.mapper.entity.util.OptionalMapper;
import com.tms.profilemanager.app.mapper.entity.util.SetModificationMapper;
import com.tms.profilemanager.dto.ContactlessAppDto;
import com.tms.profilemanager.dto.modification.ModificationContactlessAppDto;
import org.mapstruct.AfterMapping;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", uses = {OptionalMapper.class, SetModificationMapper.class})
public abstract class ModificationContactlessAppMapper implements EntityMapper<ModificationContactlessApp, ModificationContactlessAppDto> {

    public abstract ModificationContactlessAppDto toDto(ModificationContactlessApp entity);

    @AfterMapping
    protected void afterToDto(@MappingTarget ModificationContactlessAppDto.ModificationContactlessAppDtoBuilder<?, ?> dto, ModificationContactlessApp entity) {
        dto.modificationId(entity.getModificationContactlessAppsPaymentSystem().getPaymentSystemId());
        dto.contactlessAppsId(entity.getContactlessAppId());
    }

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "terminalType", ignore = true)
    @Mapping(target = "tags", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    public abstract void patchModificationContactlessApp(ModificationContactlessApp modificationContactlessApp, @MappingTarget ContactlessAppDto entity);

    @AfterMapping
    public void afterPatchModificationContactlessApp(ModificationContactlessApp modificationContactlessApp, @MappingTarget ContactlessAppDto entity) {
        if (modificationContactlessApp.getTerminalType() != null && !modificationContactlessApp.getTerminalType().isIgnored()) {
            entity.setTerminalType(modificationContactlessApp.getTerminalType().getValue());
        }
        if (modificationContactlessApp.getTags() != null && !modificationContactlessApp.getTags().isIgnored()) {
            entity.setTags(modificationContactlessApp.getTags().getValue());
        }
    }
}
