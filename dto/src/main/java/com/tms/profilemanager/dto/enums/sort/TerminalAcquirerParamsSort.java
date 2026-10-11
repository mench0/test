package com.tms.profilemanager.dto.enums.sort;

import com.tms.profilemanager.dto.AcquirerDto;
import com.tms.profilemanager.dto.TerminalAcquirerParamsDto;
import com.tms.profilemanager.dto.filter.SearchRequest;

import java.util.List;

import static com.tms.profilemanager.dto.enums.sort.AcquirerSort.CREATED_AT;

public enum TerminalAcquirerParamsSort implements AvailableSorts<TerminalAcquirerParamsDto, TerminalAcquirerParamsSort> {

    CREATED_AT;
    @Override
    public List<SearchRequest.Sort<TerminalAcquirerParamsSort>> getDefaultSorts() {
        return List.of(SearchRequest.Sort.of(CREATED_AT, SearchRequest.SortDirection.DESC));
    }
}
