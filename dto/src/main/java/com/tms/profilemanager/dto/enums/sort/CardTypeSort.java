package com.tms.profilemanager.dto.enums.sort;

import com.tms.profilemanager.dto.CardTypeDto;
import com.tms.profilemanager.dto.filter.SearchRequest;

import java.util.List;

public enum CardTypeSort implements AvailableSorts<CardTypeDto, CardTypeSort>{
    CREATED_AT;

    @Override
    public List<SearchRequest.Sort<CardTypeSort>> getDefaultSorts() {
        return List.of(SearchRequest.Sort.of(CREATED_AT, SearchRequest.SortDirection.DESC));
    }
}
