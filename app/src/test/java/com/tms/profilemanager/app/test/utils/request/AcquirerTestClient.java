package com.tms.profilemanager.app.test.utils.request;

import com.fasterxml.jackson.core.type.TypeReference;
import com.tms.profilemanager.dto.AcquirerDto;
import com.tms.profilemanager.dto.create.AcquirerCreateDto;
import com.tms.profilemanager.dto.filter.AcquirerFilterDto;
import com.tms.profilemanager.dto.patch.AcquirerPatchDto;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

@Component
public class AcquirerTestClient extends CrudTestClient<AcquirerDto, AcquirerCreateDto, AcquirerPatchDto, AcquirerFilterDto> {

    public AcquirerTestClient(RequestSender requestSender) {
        super(AcquirerDto.class, requestSender);
    }

    @Override
    protected TypeReference<Page<AcquirerDto>> getPageDtoTypeReference() {
        return new TypeReference<>() {};
    }

    @Override
    protected String getBaseUrl() {
        return "/api/acquirers";
    }
}
