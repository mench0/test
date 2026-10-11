package com.tms.profilemanager.dto.enums.sort;

import com.tms.profilemanager.dto.AcquirerDto;
import com.tms.profilemanager.dto.filter.SearchRequest;

import java.util.List;

public enum AcquirerSort implements AvailableSorts<AcquirerDto, AcquirerSort> {
    NAME,
    CREATED_AT;

    @Override
    public List<SearchRequest.Sort<AcquirerSort>> getDefaultSorts() {
        return List.of(SearchRequest.Sort.of(NAME, SearchRequest.SortDirection.ASC));
    }
}
