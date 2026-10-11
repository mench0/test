package com.tms.profilemanager.dto.enums.sort;

import com.tms.profilemanager.dto.modification.ModificationAcquirerDto;
import com.tms.profilemanager.dto.filter.SearchRequest;

import java.util.List;

public enum ModificationAcquirerSort implements AvailableSorts<ModificationAcquirerDto, ModificationAcquirerSort> {

    CREATED_AT;

    @Override
    public List<SearchRequest.Sort<ModificationAcquirerSort>> getDefaultSorts() {
        return List.of(SearchRequest.Sort.of(CREATED_AT, SearchRequest.SortDirection.DESC));
    }
}
