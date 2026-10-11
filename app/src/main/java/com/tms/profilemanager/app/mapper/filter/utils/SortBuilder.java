package com.tms.profilemanager.app.mapper.filter.utils;

import com.querydsl.core.types.Path;
import com.tms.profilemanager.dto.filter.SearchRequest;
import org.springframework.data.domain.Sort;

public class SortBuilder {

    public static Sort.Order buildOrder(Path<?> path, SearchRequest.SortDirection direction) {
        return direction.equals(SearchRequest.SortDirection.ASC)
                ? Sort.Order.asc(path.getMetadata().getName())
                : Sort.Order.desc(path.getMetadata().getName());
    }
}
