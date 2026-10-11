package com.tms.profilemanager.app.test.utils.request;

import com.fasterxml.jackson.core.type.TypeReference;
import com.tms.profilemanager.dto.MerchantDto;
import com.tms.profilemanager.dto.create.MerchantCreateDto;
import com.tms.profilemanager.dto.filter.MerchantFilterDto;
import com.tms.profilemanager.dto.patch.MerchantPatchDto;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

@Component
public class MerchantTestClient extends
        CrudTestClient<MerchantDto, MerchantCreateDto, MerchantPatchDto, MerchantFilterDto> {

    public MerchantTestClient(RequestSender requestSender) {
        super(MerchantDto.class, requestSender);
    }

    @Override
    protected TypeReference<Page<MerchantDto>> getPageDtoTypeReference() {
        return new TypeReference<>() {};
    }

    @Override
    protected String getBaseUrl() {
        return "/api/merchant";
    }
}
