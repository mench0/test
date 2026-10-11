package com.tms.profilemanager.dto.enums.sort;

import com.tms.profilemanager.dto.GeneralLimitDto;
import com.tms.profilemanager.dto.filter.SearchRequest;

import java.util.List;

public enum GeneralLimitSort implements AvailableSorts<GeneralLimitDto, GeneralLimitSort> {
    CREATED_AT;

    @Override
    public List<SearchRequest.Sort<GeneralLimitSort>> getDefaultSorts() {
        return List.of(SearchRequest.Sort.of(CREATED_AT, SearchRequest.SortDirection.DESC));
    }
}
