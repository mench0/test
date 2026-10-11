package com.tms.profilemanager.dto.enums.sort;

import com.tms.profilemanager.dto.CardRangeDto;
import com.tms.profilemanager.dto.filter.SearchRequest;

import java.util.List;

public enum CardRangeSort implements AvailableSorts<CardRangeDto, CardRangeSort> {
    CREATED_AT;

    @Override
    public List<SearchRequest.Sort<CardRangeSort>> getDefaultSorts() {
        return List.of(SearchRequest.Sort.of(CREATED_AT, SearchRequest.SortDirection.DESC));
    }
}
