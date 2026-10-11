package com.tms.profilemanager.app.test.utils.request;

import com.fasterxml.jackson.core.type.TypeReference;
import com.tms.profilemanager.dto.HumoSettingDto;
import com.tms.profilemanager.dto.create.HumoSettingCreateDto;
import com.tms.profilemanager.dto.filter.HumoSettingFilterDto;
import com.tms.profilemanager.dto.patch.HumoSettingPatchDto;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

@Component
public class HumoSettingTestClient extends CrudTestClient<HumoSettingDto, HumoSettingCreateDto, HumoSettingPatchDto, HumoSettingFilterDto> {
    public HumoSettingTestClient(RequestSender requestSender) {
        super(HumoSettingDto.class, requestSender);
    }

    @Override
    protected TypeReference<Page<HumoSettingDto>> getPageDtoTypeReference() {
        return new TypeReference<>() {};
    }

    @Override
    protected String getBaseUrl() {
        return "/api/humo-setting";
    }
}
