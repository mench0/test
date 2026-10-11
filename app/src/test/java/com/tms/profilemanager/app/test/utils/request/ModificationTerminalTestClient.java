package com.tms.profilemanager.app.test.utils.request;

import com.fasterxml.jackson.core.type.TypeReference;
import com.tms.profilemanager.dto.create.modification.ModificationTerminalCreateDto;
import com.tms.profilemanager.dto.filter.ModificationTerminalFilterDto;
import com.tms.profilemanager.dto.modification.ModificationTerminalDto;
import com.tms.profilemanager.dto.patch.modification.ModificationTerminalPatchDto;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

@Component
public class ModificationTerminalTestClient extends CrudTestClient<ModificationTerminalDto, ModificationTerminalCreateDto, ModificationTerminalPatchDto, ModificationTerminalFilterDto> {

    public ModificationTerminalTestClient(RequestSender requestSender) {
        super(ModificationTerminalDto.class, requestSender);
    }

    @Override
    protected TypeReference<Page<ModificationTerminalDto>> getPageDtoTypeReference() {
        return new TypeReference<>() {};
    }

    @Override
    protected String getBaseUrl() {
        return "/api/modification-terminal";
    }
}
