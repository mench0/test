package com.tms.profilemanager.app.service.provider.modificatiion;

import com.tms.profilemanager.app.entity.modification.ModificationTerminal;
import com.tms.profilemanager.app.mapper.entity.util.OptionalMapper;
import com.tms.profilemanager.app.service.ModificationTerminalService;
import com.tms.profilemanager.app.service.TerminalGroupService;
import com.tms.profilemanager.app.service.provider.EntityProvider;
import com.tms.profilemanager.dto.create.modification.ModificationTerminalCreateDto;
import com.tms.profilemanager.dto.patch.modification.ModificationTerminalPatchDto;
import org.mapstruct.AfterMapping;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;

@Mapper(componentModel = "spring", uses = OptionalMapper.class)
public abstract class ModificationTerminalProvider extends EntityProvider<ModificationTerminal, ModificationTerminalCreateDto, ModificationTerminalPatchDto> {

    @Autowired
    @Lazy
    TerminalGroupService terminalGroupService;
    @Autowired
    @Lazy
    ModificationTerminalService modificationTerminalService;

    @Override
    public abstract void initializeEntity(ModificationTerminalCreateDto source, @MappingTarget ModificationTerminal target);

    @AfterMapping
    protected void afterInitializeEntity(ModificationTerminalCreateDto source, @MappingTarget ModificationTerminal target) {
        terminalGroupService.existsByIdOrElseThrow(source.getGroupId());
        target.setGroupId(source.getGroupId());
    }

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    protected abstract void patchEntity(ModificationTerminalPatchDto patchDto, @MappingTarget ModificationTerminal entity);

    @AfterMapping
    protected void afterPatchEntity(ModificationTerminalPatchDto patchDto, @MappingTarget ModificationTerminal entity) {
        terminalGroupService.existsByIdOrElseThrow(patchDto.getGroupId());
        entity.setGroupId(patchDto.getGroupId());
    }

    @Override
    protected Class<ModificationTerminal> getEntityClass() {
        return ModificationTerminal.class;
    }

}
