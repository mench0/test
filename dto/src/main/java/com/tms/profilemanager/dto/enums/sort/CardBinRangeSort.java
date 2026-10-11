
package com.tms.profilemanager.dto.enums.sort;

import com.tms.profilemanager.dto.CardBinRangeDto;
import com.tms.profilemanager.dto.filter.SearchRequest;

import java.util.List;

public enum CardBinRangeSort implements AvailableSorts<CardBinRangeDto, CardBinRangeSort>{
    CREATED_AT;

    @Override
    public List<SearchRequest.Sort<CardBinRangeSort>> getDefaultSorts() {
        return List.of(SearchRequest.Sort.of(CREATED_AT, SearchRequest.SortDirection.DESC));
    }
}
