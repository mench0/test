package com.tms.profilemanager.dto.enums.sort;

import com.tms.profilemanager.dto.modification.ModificationEmvAppDto;
import com.tms.profilemanager.dto.filter.SearchRequest;

import java.util.List;

public enum ModificationEmvAppSort implements AvailableSorts<ModificationEmvAppDto, ModificationEmvAppSort> {

    CREATED_AT;

    @Override
    public List<SearchRequest.Sort<ModificationEmvAppSort>> getDefaultSorts() {
        return List.of(SearchRequest.Sort.of(CREATED_AT, SearchRequest.SortDirection.DESC));
    }
}
