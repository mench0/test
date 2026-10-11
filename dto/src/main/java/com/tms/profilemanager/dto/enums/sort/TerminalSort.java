package com.tms.profilemanager.dto.enums.sort;

import com.tms.profilemanager.dto.TerminalDto;
import com.tms.profilemanager.dto.filter.SearchRequest;

import java.util.List;

public enum TerminalSort implements AvailableSorts<TerminalDto, TerminalSort> {
    CREATED_AT;

    @Override
    public List<SearchRequest.Sort<TerminalSort>> getDefaultSorts() {
        return List.of(SearchRequest.Sort.of(CREATED_AT, SearchRequest.SortDirection.DESC));
    }
}
