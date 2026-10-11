package com.tms.profilemanager.dto.enums.sort;

import com.tms.profilemanager.dto.VendorDto;
import com.tms.profilemanager.dto.filter.SearchRequest;

import java.util.List;

public enum VendorSort implements AvailableSorts<VendorDto, VendorSort> {
    CREATED_AT;

    @Override
    public List<SearchRequest.Sort<VendorSort>> getDefaultSorts() {
        return List.of(SearchRequest.Sort.of(CREATED_AT, SearchRequest.SortDirection.DESC));
    }
}
