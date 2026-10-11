package com.tms.profilemanager.app.test.utils.request;

import com.fasterxml.jackson.core.type.TypeReference;
import com.tms.profilemanager.dto.SystemUserDto;
import com.tms.profilemanager.dto.create.SystemUserCreateDto;
import com.tms.profilemanager.dto.filter.UserFilterDto;
import com.tms.profilemanager.dto.patch.SystemUserPatchDto;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.util.Optional;

import static org.springframework.http.HttpMethod.GET;

@Component
public class SystemUserTestClient extends
        CrudTestClient<SystemUserDto, SystemUserCreateDto, SystemUserPatchDto, UserFilterDto> {

    public SystemUserTestClient(RequestSender requestSender) {
        super(SystemUserDto.class, requestSender);
    }

    @Override
    protected TypeReference<Page<SystemUserDto>> getPageDtoTypeReference() {
        return new TypeReference<>() {};
    }

    @Override
    protected String getBaseUrl() {
        return "/api/user";
    }

    @Override
    protected String getBaseUrlToCreate() {
        return getBaseUrl() + "/system";
    }

    @Override
    protected String getBaseUrlToPatch() {
        return getBaseUrl() + "/system";
    }

    public SystemUserDto getCurrentUser() {
        return requestSender.sendAndExpectOk(GET, getBaseUrl() + "/current", null, SystemUserDto.class);
    }

    public Optional<SystemUserDto> getByTerminalSerialNumber(String serialNumber) {
        return searchOne(UserFilterDto.builder()
                .terminalSerialNumber(serialNumber)
                .build());
    }
}
