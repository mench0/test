package com.tms.profilemanager.dto.enums.sort;

import com.tms.profilemanager.dto.modification.ModificationContactlessAppDto;
import com.tms.profilemanager.dto.filter.SearchRequest;

import java.util.List;

public enum ModificationContactlessAppSort implements AvailableSorts<ModificationContactlessAppDto, ModificationContactlessAppSort> {

    CREATED_AT;

    @Override
    public List<SearchRequest.Sort<ModificationContactlessAppSort>> getDefaultSorts() {
        return List.of(SearchRequest.Sort.of(CREATED_AT, SearchRequest.SortDirection.DESC));
    }
}
