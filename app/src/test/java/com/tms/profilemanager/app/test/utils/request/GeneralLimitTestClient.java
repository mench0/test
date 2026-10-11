package com.tms.profilemanager.app.test.utils.request;

import com.fasterxml.jackson.core.type.TypeReference;
import com.tms.profilemanager.dto.GeneralLimitDto;
import com.tms.profilemanager.dto.create.limit.GeneralLimitCreateDto;
import com.tms.profilemanager.dto.filter.GeneralLimitFilterDto;
import com.tms.profilemanager.dto.patch.GeneralLimitPatchDto;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

@Component
public class GeneralLimitTestClient extends CrudTestClient<GeneralLimitDto, GeneralLimitCreateDto, GeneralLimitPatchDto, GeneralLimitFilterDto> {

    public GeneralLimitTestClient(RequestSender requestSender) {
        super(GeneralLimitDto.class, requestSender);
    }

    @Override
    protected TypeReference<Page<GeneralLimitDto>> getPageDtoTypeReference() {
        return new TypeReference<>() {};
    }

    @Override
    protected String getBaseUrl() {
        return "/api/general-limit";
    }
}
