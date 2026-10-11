package com.tms.profilemanager.app.test.utils.request;

import com.fasterxml.jackson.core.type.TypeReference;
import com.tms.profilemanager.dto.ContactlessAppDto;
import com.tms.profilemanager.dto.create.ContactlessAppCreateDto;
import com.tms.profilemanager.dto.filter.ContactlessAppFilterDto;
import com.tms.profilemanager.dto.patch.ContactlessAppPatchDto;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

@Component
public class ContactlessAppTestClient extends
        CrudTestClient<ContactlessAppDto, ContactlessAppCreateDto, ContactlessAppPatchDto, ContactlessAppFilterDto> {

    public ContactlessAppTestClient(RequestSender requestSender) {
        super(ContactlessAppDto.class, requestSender);
    }

    @Override
    protected TypeReference<Page<ContactlessAppDto>> getPageDtoTypeReference() {
        return new TypeReference<>() {};
    }

    @Override
    protected String getBaseUrl() {
        return "/api/contactless-app";
    }
}
