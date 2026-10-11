package com.tms.profilemanager.app.test.utils.request;

import com.fasterxml.jackson.core.type.TypeReference;
import com.tms.profilemanager.dto.RoleDto;
import com.tms.profilemanager.dto.create.RoleCreateDto;
import com.tms.profilemanager.dto.filter.RoleFilterDto;
import com.tms.profilemanager.dto.patch.RolePatchDto;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

@Component
public class RoleTestClient extends CrudTestClient<RoleDto, RoleCreateDto, RolePatchDto, RoleFilterDto> {

    public RoleTestClient(RequestSender requestSender) {
        super(RoleDto.class, requestSender);
    }

    @Override
    protected TypeReference<Page<RoleDto>> getPageDtoTypeReference() {
        return new TypeReference<>() {};
    }

    @Override
    protected String getBaseUrl() {
        return "/api/role";
    }
}
