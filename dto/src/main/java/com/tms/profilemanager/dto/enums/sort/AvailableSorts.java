package com.tms.profilemanager.dto.enums.sort;

import com.tms.profilemanager.dto.BaseDto;
import com.tms.profilemanager.dto.filter.SearchRequest.Sort;

import java.util.List;

public interface AvailableSorts<D extends BaseDto, S extends AvailableSorts<D, S>> {

    List<Sort<S>> getDefaultSorts();
}
