package com.tms.profilemanager.dto.enums.sort;

import com.tms.profilemanager.dto.ContactLimitDto;
import com.tms.profilemanager.dto.filter.SearchRequest;

import java.util.List;

public enum ContactLimitSort implements AvailableSorts<ContactLimitDto, ContactLimitSort> {
    CREATED_AT;

    @Override
    public List<SearchRequest.Sort<ContactLimitSort>> getDefaultSorts() {
        return List.of(SearchRequest.Sort.of(CREATED_AT, SearchRequest.SortDirection.DESC));
    }
}
