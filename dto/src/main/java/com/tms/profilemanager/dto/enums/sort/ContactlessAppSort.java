package com.tms.profilemanager.dto.enums.sort;

import com.tms.profilemanager.dto.ContactlessAppDto;
import com.tms.profilemanager.dto.filter.SearchRequest;

import java.util.List;

public enum ContactlessAppSort implements AvailableSorts<ContactlessAppDto, ContactlessAppSort> {
    CREATED_AT;

    @Override
    public List<SearchRequest.Sort<ContactlessAppSort>> getDefaultSorts() {
        return List.of(SearchRequest.Sort.of(CREATED_AT, SearchRequest.SortDirection.DESC));
    }
}
