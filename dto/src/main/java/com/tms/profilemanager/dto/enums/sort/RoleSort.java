package com.tms.profilemanager.dto.enums.sort;

import com.tms.profilemanager.dto.RoleDto;
import com.tms.profilemanager.dto.filter.SearchRequest;

import java.util.List;

public enum RoleSort implements AvailableSorts<RoleDto, RoleSort> {
    CREATED_AT,
    NAME;

    @Override
    public List<SearchRequest.Sort<RoleSort>> getDefaultSorts() {
        return List.of(SearchRequest.Sort.of(NAME, SearchRequest.SortDirection.ASC));
    }
}
