package com.tms.profilemanager.app.test.utils.request;

import com.fasterxml.jackson.core.type.TypeReference;
import com.tms.profilemanager.dto.DRLDto;
import com.tms.profilemanager.dto.create.DRLCreateDto;
import com.tms.profilemanager.dto.filter.DRLFilterDto;
import com.tms.profilemanager.dto.patch.DRLPatchDto;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

@Component
public class DRLTestClient extends CrudTestClient<DRLDto, DRLCreateDto, DRLPatchDto, DRLFilterDto> {

    public DRLTestClient(RequestSender requestSender) {
        super(DRLDto.class, requestSender);
    }

    @Override
    protected TypeReference<Page<DRLDto>> getPageDtoTypeReference() {
        return new TypeReference<>() {};
    }

    @Override
    protected String getBaseUrl() {
        return "/api/drl";
    }
}
