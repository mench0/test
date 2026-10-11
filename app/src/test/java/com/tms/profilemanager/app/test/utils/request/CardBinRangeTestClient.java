package com.tms.profilemanager.app.test.utils.request;

import com.fasterxml.jackson.core.type.TypeReference;
import com.tms.profilemanager.dto.CardBinRangeDto;
import com.tms.profilemanager.dto.create.dictionary.CardBinRangeCreateDto;
import com.tms.profilemanager.dto.filter.CardBinRangeFilterDto;
import com.tms.profilemanager.dto.patch.dictionary.CardBinRangePatchDto;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

@Component
public class CardBinRangeTestClient extends CrudTestClient<CardBinRangeDto, CardBinRangeCreateDto, CardBinRangePatchDto, CardBinRangeFilterDto> {

    public CardBinRangeTestClient(RequestSender requestSender) {
        super(CardBinRangeDto.class, requestSender);
    }

    @Override
    protected TypeReference<Page<CardBinRangeDto>> getPageDtoTypeReference() {
        return new TypeReference<>() {};
    }

    @Override
    protected String getBaseUrl() {
        return "/api/card/bin-range";
    }
}
