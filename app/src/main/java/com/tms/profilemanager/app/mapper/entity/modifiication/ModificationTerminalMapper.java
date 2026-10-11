package com.tms.profilemanager.app.mapper.entity.modifiication;

import com.tms.profilemanager.app.entity.modification.ModificationTerminal;
import com.tms.profilemanager.app.mapper.entity.CountryMapper;
import com.tms.profilemanager.app.mapper.entity.EntityMapper;
import com.tms.profilemanager.app.mapper.entity.TerminalMapper;
import com.tms.profilemanager.app.service.provider.TerminalProvider;
import com.tms.profilemanager.dto.modification.ModificationTerminalDto;
import com.tms.profilemanager.dto.view.TerminalView;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;

import java.util.List;

@Mapper(componentModel = "spring")
public abstract class ModificationTerminalMapper implements EntityMapper<ModificationTerminal, ModificationTerminalDto> {

    @Autowired
    @Lazy
    TerminalMapper terminalMapper;
    @Autowired
    @Lazy
    CountryMapper countryMapper;
    @Autowired
    @Lazy
    TerminalProvider terminalProvider;

    public TerminalView terminalViewModificationMapping(TerminalView terminalView, List<ModificationTerminal> modificationTerminals) {
        modificationTerminals.forEach(it -> patchModificationTerminalView(it, terminalView));
        return terminalView;
    }

    public void patchModificationTerminalView(ModificationTerminal modificationTerminal, @MappingTarget TerminalView view) {
        if (modificationTerminal.getCountry() != null) {
            view.setCountry(countryMapper.toCountryDto(modificationTerminal.getCountry()));
        }
        if (modificationTerminal.getServicePin() != null) {
            view.setServicePin(modificationTerminal.getServicePin());
        }
    }

    @Override
    @Mapping(target = "groupId", ignore = true)
    public abstract ModificationTerminalDto toDto(ModificationTerminal entity);

    @AfterMapping
    protected void afterToDto(@MappingTarget ModificationTerminalDto.ModificationTerminalDtoBuilder<?, ?> dto, ModificationTerminal entity) {
        dto.groupId(entity.getGroupId());
    }
}
