package com.tms.profilemanager.dto.enums.sort;

import com.tms.profilemanager.dto.CertificateDto;
import com.tms.profilemanager.dto.filter.SearchRequest;

import java.util.List;

public enum CertificateSort implements AvailableSorts<CertificateDto, CertificateSort> {
    CREATED_AT;

    @Override
    public List<SearchRequest.Sort<CertificateSort>> getDefaultSorts() {
        return List.of(SearchRequest.Sort.of(CREATED_AT, SearchRequest.SortDirection.DESC));
    }
}
