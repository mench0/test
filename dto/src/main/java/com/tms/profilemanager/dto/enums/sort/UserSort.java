package com.tms.profilemanager.dto.enums.sort;

import com.tms.profilemanager.dto.UserDto;
import com.tms.profilemanager.dto.filter.SearchRequest;

import java.util.List;

public enum UserSort implements AvailableSorts<UserDto, UserSort> {
    CREATED_AT,
    FULL_NAME;

    @Override
    public List<SearchRequest.Sort<UserSort>> getDefaultSorts() {
        return List.of(SearchRequest.Sort.of(FULL_NAME, SearchRequest.SortDirection.ASC));
    }
}
