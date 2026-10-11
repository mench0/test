package com.tms.profilemanager.dto.enums.sort;

import com.tms.profilemanager.dto.TerminalGroupDto;
import com.tms.profilemanager.dto.filter.SearchRequest;

import java.util.List;

public enum TerminalGroupSort implements AvailableSorts<TerminalGroupDto, TerminalGroupSort> {
    CREATED_AT,
    NAME;

    @Override
    public List<SearchRequest.Sort<TerminalGroupSort>> getDefaultSorts() {
        return List.of(SearchRequest.Sort.of(NAME, SearchRequest.SortDirection.ASC));
    }
}
