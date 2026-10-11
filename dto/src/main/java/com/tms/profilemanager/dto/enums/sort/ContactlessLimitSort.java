package com.tms.profilemanager.dto.enums.sort;

import com.tms.profilemanager.dto.ContactlessLimitDto;
import com.tms.profilemanager.dto.filter.SearchRequest;

import java.util.List;

public enum ContactlessLimitSort implements AvailableSorts<ContactlessLimitDto, ContactlessLimitSort> {
    CREATED_AT;

    @Override
    public List<SearchRequest.Sort<ContactlessLimitSort>> getDefaultSorts() {
        return List.of(SearchRequest.Sort.of(CREATED_AT, SearchRequest.SortDirection.DESC));
    }
}
