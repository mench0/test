package com.tms.profilemanager.app.test.utils.request;

import com.fasterxml.jackson.core.type.TypeReference;
import com.tms.profilemanager.dto.modification.ModificationEmvAppDto;
import com.tms.profilemanager.dto.create.modification.ModificationEmvAppCreateDto;
import com.tms.profilemanager.dto.filter.ModificationEmvAppFilterDto;
import com.tms.profilemanager.dto.patch.modification.ModificationEmvAppPatchDto;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

@Component
public class ModificationEmvAppTestClient extends CrudTestClient<ModificationEmvAppDto, ModificationEmvAppCreateDto, ModificationEmvAppPatchDto, ModificationEmvAppFilterDto> {

    public ModificationEmvAppTestClient(RequestSender requestSender) {
        super(ModificationEmvAppDto.class, requestSender);
    }

    @Override
    protected TypeReference<Page<ModificationEmvAppDto>> getPageDtoTypeReference() {
        return new TypeReference<>() {};
    }

    @Override
    protected String getBaseUrl() {
        return "/api/modification-emv-app";
    }
}
