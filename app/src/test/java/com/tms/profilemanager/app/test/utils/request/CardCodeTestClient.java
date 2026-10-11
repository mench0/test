package com.tms.profilemanager.app.test.utils.request;

import com.fasterxml.jackson.core.type.TypeReference;
import com.tms.profilemanager.dto.CardCodeDto;
import com.tms.profilemanager.dto.create.dictionary.CardCodeCreateDto;
import com.tms.profilemanager.dto.filter.CardCodeFilterDto;
import com.tms.profilemanager.dto.patch.dictionary.CardCodePatchDto;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.util.List;

import static org.springframework.http.HttpMethod.GET;

@Component
public class CardCodeTestClient extends CrudTestClient<CardCodeDto, CardCodeCreateDto, CardCodePatchDto, CardCodeFilterDto> {

    public CardCodeTestClient(RequestSender requestSender) {
        super(CardCodeDto.class, requestSender);
    }

    public List<CardCodeDto.CardTypeCodesDto> getGroupedCardCodes() {
        return requestSender.sendAndExpectOk(
                GET,
                getBaseUrl() + "/grouped-by-types",
                null, getPageGroupedCardTypeDtoTypeReference());
    }

    @Override
    protected TypeReference<Page<CardCodeDto>> getPageDtoTypeReference() {
        return new TypeReference<>() {};
    }

    protected TypeReference<List<CardCodeDto.CardTypeCodesDto>> getPageGroupedCardTypeDtoTypeReference() {
        return new TypeReference<>() {};
    }

    @Override
    protected String getBaseUrl() {
        return "/api/card/code";
    }
}
