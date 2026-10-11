package com.tms.profilemanager.app.test.utils.request;

import com.fasterxml.jackson.core.type.TypeReference;
import com.tms.profilemanager.dto.LicenseDto;
import com.tms.profilemanager.dto.create.LicenseCreateDto;
import com.tms.profilemanager.dto.filter.LicenseFilterDto;
import com.tms.profilemanager.dto.patch.LicensePatchDto;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import static org.springframework.http.HttpMethod.GET;

@Component
public class LicenseTestClient extends CrudTestClient<LicenseDto, LicenseCreateDto, LicensePatchDto, LicenseFilterDto> {

    public LicenseTestClient(RequestSender requestSender) {
        super(LicenseDto.class, requestSender);
    }

    @Override
    protected TypeReference<Page<LicenseDto>> getPageDtoTypeReference() {
        return new TypeReference<>() {};
    }

    public LicenseDto getBySerialNumber(String serialNumber) {
        return requestSender.sendAndExpectOk(
                GET, getBaseUrl() + "/by-sn/" + serialNumber, null, LicenseDto.class);
    }

    @Override
    protected String getBaseUrl() {
        return "/api/license";
    }
}
