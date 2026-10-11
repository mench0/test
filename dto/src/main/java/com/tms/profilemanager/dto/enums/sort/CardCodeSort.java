
package com.tms.profilemanager.dto.enums.sort;

import com.tms.profilemanager.dto.CardCodeDto;
import com.tms.profilemanager.dto.filter.SearchRequest;

import java.util.List;

public enum CardCodeSort implements AvailableSorts<CardCodeDto, CardCodeSort>{
    CREATED_AT;

    @Override
    public List<SearchRequest.Sort<CardCodeSort>> getDefaultSorts() {
        return List.of(SearchRequest.Sort.of(CREATED_AT, SearchRequest.SortDirection.DESC));
    }
}
