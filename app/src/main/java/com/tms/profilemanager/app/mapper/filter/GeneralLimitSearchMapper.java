package com.tms.profilemanager.app.mapper.filter;

import com.querydsl.core.types.Predicate;
import com.tms.profilemanager.app.config.security.AuthExpressionsHolder;
import com.tms.profilemanager.app.mapper.filter.utils.PredicateBuilder;
import com.tms.profilemanager.dto.enums.sort.GeneralLimitSort;
import com.tms.profilemanager.dto.filter.GeneralLimitFilterDto;
import com.tms.profilemanager.dto.filter.SearchRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import static com.tms.profilemanager.app.entity.QGeneralLimit.generalLimit;
import static com.tms.profilemanager.app.mapper.filter.utils.SortBuilder.buildOrder;
import static com.tms.profilemanager.dto.enums.Permission.PAYMENT_SYSTEM_READ;

@Component
public class GeneralLimitSearchMapper extends SearchMapper<GeneralLimitFilterDto, GeneralLimitSort> {
    @Override
    protected Predicate toPredicateExceptSubFilters(GeneralLimitFilterDto filter) {
        return PredicateBuilder.builder()
                .in(generalLimit.id, filter.getIds())
                .build();
    }

    @Override
    public Predicate getAccessControlPredicate() {
        return AuthExpressionsHolder.hasPermission(PAYMENT_SYSTEM_READ) ? null : PredicateBuilder.buildAlwaysFalsePredicate();
    }

    @Override
    protected Sort.Order toOrder(GeneralLimitSort sortName, SearchRequest.SortDirection direction) {
        return switch (sortName) {
            case CREATED_AT -> buildOrder(generalLimit.createdAt, direction);
        };
    }

    @Override
    protected Class<GeneralLimitFilterDto> getFilterDtoClass() {
        return GeneralLimitFilterDto.class;
    }
}
