package com.tms.profilemanager.app.test.utils.request;

import com.fasterxml.jackson.core.type.TypeReference;
import com.tms.profilemanager.dto.CardTypeDto;
import com.tms.profilemanager.dto.create.dictionary.CardTypeCreateDto;
import com.tms.profilemanager.dto.filter.CardTypeFilterDto;
import com.tms.profilemanager.dto.patch.dictionary.CardTypePatchDto;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

@Component
public class CardTypeTestClient extends CrudTestClient<CardTypeDto, CardTypeCreateDto, CardTypePatchDto, CardTypeFilterDto> {

    public CardTypeTestClient(RequestSender requestSender) {
        super(CardTypeDto.class, requestSender);
    }

    @Override
    protected TypeReference<Page<CardTypeDto>> getPageDtoTypeReference() {
        return new TypeReference<>() {};
    }

    @Override
    protected String getBaseUrl() {
        return "/api/cards/types";
    }
}
