package com.tms.profilemanager.app.test.utils.request;

import com.fasterxml.jackson.core.type.TypeReference;
import com.tms.profilemanager.dto.TerminalGroupDto;
import com.tms.profilemanager.dto.create.TerminalGroupCreateDto;
import com.tms.profilemanager.dto.filter.TerminalGroupFilterDto;
import com.tms.profilemanager.dto.patch.TerminalGroupPatchDto;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

@Component
public class TerminalGroupTestClient extends
        CrudTestClient<TerminalGroupDto, TerminalGroupCreateDto, TerminalGroupPatchDto, TerminalGroupFilterDto> {

    public TerminalGroupTestClient(RequestSender requestSender) {
        super(TerminalGroupDto.class, requestSender);
    }

    @Override
    protected TypeReference<Page<TerminalGroupDto>> getPageDtoTypeReference() {
        return new TypeReference<>() {};
    }

    @Override
    protected String getBaseUrl() {
        return "/api/terminal-group";
    }
}
