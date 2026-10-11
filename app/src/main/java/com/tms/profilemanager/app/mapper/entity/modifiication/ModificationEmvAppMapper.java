package com.tms.profilemanager.app.mapper.entity.modifiication;

import com.tms.profilemanager.app.entity.modification.ModificationEmvApp;
import com.tms.profilemanager.app.mapper.entity.EntityMapper;
import com.tms.profilemanager.app.mapper.entity.util.OptionalMapper;
import com.tms.profilemanager.app.service.EMVAppService;
import com.tms.profilemanager.dto.EMVAppDto;
import com.tms.profilemanager.dto.modification.ModificationEmvAppDto;
import org.mapstruct.AfterMapping;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;

@Mapper(componentModel = "spring", uses = OptionalMapper.class)
public abstract class ModificationEmvAppMapper implements EntityMapper<ModificationEmvApp, ModificationEmvAppDto> {

    @Autowired @Lazy
    EMVAppService emvAppService;

    public abstract ModificationEmvAppDto toDto(ModificationEmvApp entity);

    @AfterMapping
    protected void afterToDto(@MappingTarget ModificationEmvAppDto.ModificationEmvAppDtoBuilder<?, ?> dto, ModificationEmvApp entity) {
        dto.modificationId(entity.getModificationEmvAppsPaymentSystem().getPaymentSystemId());
        dto.emvAppsId(emvAppService.getByIdOrElseThrow(entity.getEmvAppId()).getId());
    }

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "terminalType", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    public abstract void patchModificationEmvApp(ModificationEmvApp modificationEmvApp, @MappingTarget EMVAppDto entity);

    @AfterMapping
    public void afterPatchModificationEmvApp(ModificationEmvApp modificationEmvApp, @MappingTarget EMVAppDto entity) {
        if(modificationEmvApp.getTerminalType() != null && !modificationEmvApp.getTerminalType().isIgnored()) {
            entity.setTerminalType(modificationEmvApp.getTerminalType().getValue());
        }
    }
}
