package com.tms.profilemanager.app.test.utils.request;

import com.fasterxml.jackson.core.type.TypeReference;
import com.tms.profilemanager.dto.ContactlessLimitDto;
import com.tms.profilemanager.dto.create.limit.ContactlessLimitCreateDto;
import com.tms.profilemanager.dto.filter.ContactlessLimitFilterDto;
import com.tms.profilemanager.dto.patch.ContactlessLimitPatchDto;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

@Component
public class ContactlessLimitTestClient extends
        CrudTestClient<ContactlessLimitDto, ContactlessLimitCreateDto, ContactlessLimitPatchDto, ContactlessLimitFilterDto> {

    public ContactlessLimitTestClient(RequestSender requestSender) {
        super(ContactlessLimitDto.class, requestSender);
    }

    @Override
    protected TypeReference<Page<ContactlessLimitDto>> getPageDtoTypeReference() {
        return new TypeReference<>() {};
    }

    @Override
    protected String getBaseUrl() {
        return "/api/contactless-limit";
    }
}
