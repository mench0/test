package com.tms.profilemanager.dto.enums.sort;

import com.tms.profilemanager.dto.MerchantDto;
import com.tms.profilemanager.dto.filter.SearchRequest;

import java.util.List;

public enum MerchantSort implements AvailableSorts<MerchantDto, MerchantSort> {
    CREATED_AT,
    NAME;

    @Override
    public List<SearchRequest.Sort<MerchantSort>> getDefaultSorts() {
        return List.of(SearchRequest.Sort.of(CREATED_AT, SearchRequest.SortDirection.DESC));
    }
}
