package com.tms.profilemanager.app.mapper.filter;

import com.querydsl.core.types.Predicate;
import com.tms.profilemanager.app.mapper.filter.utils.PredicateBuilder;
import com.tms.profilemanager.dto.enums.sort.HumoSettingSort;
import com.tms.profilemanager.dto.filter.HumoSettingFilterDto;
import com.tms.profilemanager.dto.filter.SearchRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import static com.tms.profilemanager.app.config.security.AuthExpressionsHolder.hasPermission;
import static com.tms.profilemanager.app.entity.QHumoSetting.humoSetting;
import static com.tms.profilemanager.app.mapper.filter.utils.SortBuilder.buildOrder;
import static com.tms.profilemanager.dto.enums.Permission.TERMINAL_READ;

@Component
public class HumoSettingSearchMapper extends SearchMapper<HumoSettingFilterDto, HumoSettingSort> {

    @Override
    protected Predicate toPredicateExceptSubFilters(HumoSettingFilterDto filter) {
        return PredicateBuilder.builder()
                .in(humoSetting.id, filter.getIds())
                .build();
    }

    @Override
    public Predicate getAccessControlPredicate() {
        return hasPermission(TERMINAL_READ) ? null : PredicateBuilder.buildAlwaysFalsePredicate();
    }

    @Override
    protected Sort.Order toOrder(HumoSettingSort sortName, SearchRequest.SortDirection direction) {
        return switch (sortName) {
            case CREATED_AT -> buildOrder(humoSetting.createdAt, direction);
        };
    }

    @Override
    protected Class<HumoSettingFilterDto> getFilterDtoClass() {
        return HumoSettingFilterDto.class;
    }
}
