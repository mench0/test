package com.tms.profilemanager.app.test.utils.request;

import com.fasterxml.jackson.core.type.TypeReference;
import com.tms.profilemanager.dto.VendorDto;
import com.tms.profilemanager.dto.create.VendorCreateDto;
import com.tms.profilemanager.dto.filter.VendorFilterDto;
import com.tms.profilemanager.dto.patch.VendorPatchDto;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

@Component
public class VendorTestClient extends CrudTestClient<VendorDto, VendorCreateDto, VendorPatchDto, VendorFilterDto> {

    public VendorTestClient(RequestSender requestSender) {
        super(VendorDto.class, requestSender);
    }

    @Override
    protected TypeReference<Page<VendorDto>> getPageDtoTypeReference() {
        return new TypeReference<>() {};
    }

    @Override
    protected String getBaseUrl() {
        return "/api/vendor";
    }
}
