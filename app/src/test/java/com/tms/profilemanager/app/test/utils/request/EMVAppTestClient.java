package com.tms.profilemanager.app.test.utils.request;

import com.fasterxml.jackson.core.type.TypeReference;
import com.tms.profilemanager.dto.EMVAppDto;
import com.tms.profilemanager.dto.create.EMVAppCreateDto;
import com.tms.profilemanager.dto.filter.EMVAppFilterDto;
import com.tms.profilemanager.dto.patch.EMVAppPatchDto;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

@Component
public class EMVAppTestClient extends CrudTestClient<EMVAppDto, EMVAppCreateDto, EMVAppPatchDto, EMVAppFilterDto> {

    public EMVAppTestClient(RequestSender requestSender) {
        super(EMVAppDto.class, requestSender);
    }

    @Override
    protected TypeReference<Page<EMVAppDto>> getPageDtoTypeReference() {
        return new TypeReference<>() {};
    }

    @Override
    protected String getBaseUrl() {
        return "/api/emv-app";
    }
}
