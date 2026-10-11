package com.tms.profilemanager.app.test.utils.request;

import com.fasterxml.jackson.core.type.TypeReference;
import com.tms.profilemanager.dto.CardRangeDto;
import com.tms.profilemanager.dto.create.CardRangeCreateDto;
import com.tms.profilemanager.dto.filter.CardRangeFilterDto;
import com.tms.profilemanager.dto.patch.CardRangePatchDto;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

@Component
public class CardRangeTestClient extends
        CrudTestClient<CardRangeDto, CardRangeCreateDto, CardRangePatchDto, CardRangeFilterDto> {

    public CardRangeTestClient(RequestSender requestSender) {
        super(CardRangeDto.class, requestSender);
    }

    @Override
    protected TypeReference<Page<CardRangeDto>> getPageDtoTypeReference() {
        return new TypeReference<>() {};
    }

    @Override
    protected String getBaseUrl() {
        return "/api/card-range";
    }
}
