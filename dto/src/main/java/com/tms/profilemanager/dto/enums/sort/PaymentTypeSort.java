package com.tms.profilemanager.dto.enums.sort;

import com.tms.profilemanager.dto.PaymentTypeDto;
import com.tms.profilemanager.dto.filter.SearchRequest;

import java.util.List;

public enum PaymentTypeSort implements AvailableSorts<PaymentTypeDto, PaymentTypeSort>{
    CREATED_AT;

    @Override
    public List<SearchRequest.Sort<PaymentTypeSort>> getDefaultSorts() {
        return List.of(SearchRequest.Sort.of(CREATED_AT, SearchRequest.SortDirection.DESC));
    }
}
