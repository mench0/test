package com.tms.profilemanager.app.test.utils.request;

import com.fasterxml.jackson.core.type.TypeReference;
import com.tms.profilemanager.dto.modification.ModificationContactlessAppDto;
import com.tms.profilemanager.dto.create.modification.ModificationContactlessAppCreateDto;
import com.tms.profilemanager.dto.filter.ModificationContactlessAppFilterDto;
import com.tms.profilemanager.dto.patch.modification.ModificationContactlessAppPatchDto;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

@Component
public class ModificationContactlessAppTestClient extends CrudTestClient<ModificationContactlessAppDto, ModificationContactlessAppCreateDto, ModificationContactlessAppPatchDto, ModificationContactlessAppFilterDto> {

    public ModificationContactlessAppTestClient(RequestSender requestSender) {
        super(ModificationContactlessAppDto.class, requestSender);
    }

    @Override
    protected TypeReference<Page<ModificationContactlessAppDto>> getPageDtoTypeReference() {
        return new TypeReference<>() {};
    }

    @Override
    protected String getBaseUrl() {
        return "/api/modification-contactless-app";
    }
}
