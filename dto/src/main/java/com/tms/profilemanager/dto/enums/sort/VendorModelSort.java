package com.tms.profilemanager.dto.enums.sort;

import com.tms.profilemanager.dto.VendorModelDto;
import com.tms.profilemanager.dto.filter.SearchRequest;

import java.util.List;

public enum VendorModelSort implements AvailableSorts<VendorModelDto, VendorModelSort> {
    CREATED_AT;

    @Override
    public List<SearchRequest.Sort<VendorModelSort>> getDefaultSorts() {
        return List.of(SearchRequest.Sort.of(CREATED_AT, SearchRequest.SortDirection.DESC));
    }
}
