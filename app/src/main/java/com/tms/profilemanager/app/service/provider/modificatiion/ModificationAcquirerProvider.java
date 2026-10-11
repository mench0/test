package com.tms.profilemanager.app.service.provider.modificatiion;

import com.tms.profilemanager.app.entity.modification.ModificationAcquirer;
import com.tms.profilemanager.app.mapper.entity.util.EnumFieldModificationMapper;
import com.tms.profilemanager.app.mapper.entity.util.OptionalMapper;
import com.tms.profilemanager.app.service.AcquirerService;
import com.tms.profilemanager.app.service.ModificationAcquirerService;
import com.tms.profilemanager.app.service.TerminalGroupService;
import com.tms.profilemanager.app.service.provider.EntityProvider;
import com.tms.profilemanager.dto.create.modification.ModificationAcquirerCreateDto;
import com.tms.profilemanager.dto.patch.modification.ModificationAcquirerPatchDto;
import org.mapstruct.AfterMapping;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;

@Mapper(componentModel = "spring", uses = {OptionalMapper.class, EnumFieldModificationMapper.class})
public abstract class ModificationAcquirerProvider extends EntityProvider<ModificationAcquirer, ModificationAcquirerCreateDto, ModificationAcquirerPatchDto> {

    @Autowired
    @Lazy
    TerminalGroupService terminalGroupService;
    @Autowired
    @Lazy
    ModificationAcquirerService modificationAcquirerService;
    @Autowired @Lazy
    AcquirerService acquirerService;

    @Override
    public abstract void initializeEntity(ModificationAcquirerCreateDto source, @MappingTarget ModificationAcquirer target);

    @AfterMapping
    protected void afterInitializeEntity(ModificationAcquirerCreateDto source, @MappingTarget ModificationAcquirer target) {
        acquirerService.existsByIdOrElseThrow(source.getAcquirerId());
        target.setAcquirerId(source.getAcquirerId());
        terminalGroupService.existsByIdOrElseThrow(source.getGroupId());
        target.setGroupId(source.getGroupId());
    }

    @Override
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    public abstract void patchEntity(ModificationAcquirerPatchDto source, @MappingTarget ModificationAcquirer target);

    @AfterMapping
    public void afterPatchEntity(ModificationAcquirerPatchDto source, @MappingTarget ModificationAcquirer target) {
        acquirerService.existsByIdOrElseThrow(source.getAcquirerId());
        target.setAcquirerId(source.getAcquirerId());
    }

    @Override
    protected Class<ModificationAcquirer> getEntityClass() {
        return ModificationAcquirer.class;
    }

}
