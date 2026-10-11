package com.tms.profilemanager.app.test.utils.request;

import com.fasterxml.jackson.core.type.TypeReference;
import com.tms.profilemanager.dto.create.modification.ModificationAcquirerCreateDto;
import com.tms.profilemanager.dto.filter.ModificationAcquirerFilterDto;
import com.tms.profilemanager.dto.modification.ModificationAcquirerDto;
import com.tms.profilemanager.dto.patch.modification.ModificationAcquirerPatchDto;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

@Component
public class ModificationAcquirerTestClient extends CrudTestClient<ModificationAcquirerDto, ModificationAcquirerCreateDto, ModificationAcquirerPatchDto, ModificationAcquirerFilterDto> {

    public ModificationAcquirerTestClient(RequestSender requestSender) {
        super(ModificationAcquirerDto.class, requestSender);
    }

    @Override
    protected TypeReference<Page<ModificationAcquirerDto>> getPageDtoTypeReference() {
        return new TypeReference<>() {};
    }

    @Override
    protected String getBaseUrl() {
        return "/api/modification-acquirer";
    }
}
