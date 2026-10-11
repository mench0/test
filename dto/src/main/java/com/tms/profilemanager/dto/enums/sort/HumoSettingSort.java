package com.tms.profilemanager.dto.enums.sort;

import com.tms.profilemanager.dto.HumoSettingDto;
import com.tms.profilemanager.dto.filter.SearchRequest;

import java.util.List;

public enum HumoSettingSort implements AvailableSorts<HumoSettingDto, HumoSettingSort> {
    CREATED_AT;

    @Override
    public List<SearchRequest.Sort<HumoSettingSort>> getDefaultSorts() {
        return List.of(SearchRequest.Sort.of(CREATED_AT, SearchRequest.SortDirection.DESC));
    }
}
