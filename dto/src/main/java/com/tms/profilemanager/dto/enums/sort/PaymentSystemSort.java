package com.tms.profilemanager.dto.enums.sort;

import com.tms.profilemanager.dto.PaymentSystemDto;
import com.tms.profilemanager.dto.filter.SearchRequest;

import java.util.List;

public enum PaymentSystemSort implements AvailableSorts<PaymentSystemDto, PaymentSystemSort> {
    CREATED_AT,
    NAME;

    @Override
    public List<SearchRequest.Sort<PaymentSystemSort>> getDefaultSorts() {
        return List.of(SearchRequest.Sort.of(CREATED_AT, SearchRequest.SortDirection.DESC));
    }
}
