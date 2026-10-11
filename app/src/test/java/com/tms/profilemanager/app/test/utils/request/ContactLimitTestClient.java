package com.tms.profilemanager.app.test.utils.request;

import com.fasterxml.jackson.core.type.TypeReference;
import com.tms.profilemanager.dto.ContactLimitDto;
import com.tms.profilemanager.dto.create.limit.ContactLimitCreateDto;
import com.tms.profilemanager.dto.filter.ContactLimitFilterDto;
import com.tms.profilemanager.dto.patch.ContactLimitPatchDto;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

@Component
public class ContactLimitTestClient extends
        CrudTestClient<ContactLimitDto, ContactLimitCreateDto, ContactLimitPatchDto, ContactLimitFilterDto> {

    public ContactLimitTestClient(RequestSender requestSender) {
        super(ContactLimitDto.class, requestSender);
    }

    @Override
    protected TypeReference<Page<ContactLimitDto>> getPageDtoTypeReference() {
        return new TypeReference<>() {};
    }

    @Override
    protected String getBaseUrl() {
        return "/api/contact-limit";
    }
}
