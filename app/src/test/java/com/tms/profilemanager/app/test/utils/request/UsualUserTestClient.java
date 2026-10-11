package com.tms.profilemanager.app.test.utils.request;

import com.fasterxml.jackson.core.type.TypeReference;
import com.tms.profilemanager.dto.UsualUserDto;
import com.tms.profilemanager.dto.request.ChangeUserPasswordRequestDto;
import com.tms.profilemanager.dto.create.UsualUserCreateDto;
import com.tms.profilemanager.dto.filter.UserFilterDto;
import com.tms.profilemanager.dto.patch.UsualUserPatchDto;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.PUT;

@Component
public class UsualUserTestClient extends
        CrudTestClient<UsualUserDto, UsualUserCreateDto, UsualUserPatchDto, UserFilterDto> {

    public UsualUserTestClient(RequestSender requestSender) {
        super(UsualUserDto.class, requestSender);
    }

    @Override
    protected TypeReference<Page<UsualUserDto>> getPageDtoTypeReference() {
        return new TypeReference<>() {};
    }

    @Override
    protected String getBaseUrl() {
        return "/api/user";
    }

    @Override
    protected String getBaseUrlToCreate() {
        return getBaseUrl() + "/usual";
    }

    @Override
    protected String getBaseUrlToPatch() {
        return getBaseUrl() + "/usual";
    }

    public UsualUserDto getCurrentUser() {
        return requestSender.sendAndExpectOk(GET, getBaseUrl() + "/current", null, UsualUserDto.class);
    }

    public HttpStatus sendPasswordResetLink(String email) {
        return HttpStatus.valueOf(requestSender.send(GET, getBaseUrl() + "/usual/password/send-reset-link?email=" + email, null)
                .getResponse().getStatus());
    }

    public HttpStatus changePassword(ChangeUserPasswordRequestDto command) {
        return HttpStatus.valueOf(requestSender.send(PUT, getBaseUrl() + "/usual/password/change", command)
                .getResponse().getStatus());
    }
}
