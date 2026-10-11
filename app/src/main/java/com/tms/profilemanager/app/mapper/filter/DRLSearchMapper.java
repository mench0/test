package com.tms.profilemanager.app.mapper.filter;

import com.querydsl.core.types.Predicate;
import com.tms.profilemanager.app.config.security.AuthExpressionsHolder;
import com.tms.profilemanager.app.mapper.filter.utils.PredicateBuilder;
import com.tms.profilemanager.dto.enums.sort.DRLSort;
import com.tms.profilemanager.dto.filter.DRLFilterDto;
import com.tms.profilemanager.dto.filter.SearchRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import static com.tms.profilemanager.app.entity.QDRL.dRL;
import static com.tms.profilemanager.app.mapper.filter.utils.SortBuilder.buildOrder;
import static com.tms.profilemanager.dto.enums.Permission.PAYMENT_SYSTEM_READ;

@Component
public class DRLSearchMapper extends SearchMapper<DRLFilterDto, DRLSort> {

    @Override
    protected Predicate toPredicateExceptSubFilters(DRLFilterDto filter) {
        return PredicateBuilder.builder()
                .in(dRL.id, filter.getIds())
                .eq(dRL.archived, filter.getArchived())
                .in(dRL.contactlessLimit.id, filter.getContactlessLimitIds())
                .build();
    }

    @Override
    public Predicate getAccessControlPredicate() {
        return AuthExpressionsHolder.hasPermission(PAYMENT_SYSTEM_READ) ? null : PredicateBuilder.buildAlwaysFalsePredicate();
    }

    @Override
    protected Sort.Order toOrder(DRLSort sortName, SearchRequest.SortDirection direction) {
        return switch (sortName) {
            case CREATED_AT -> buildOrder(dRL.createdAt, direction);
        };
    }

    @Override
    protected Class<DRLFilterDto> getFilterDtoClass() {
        return DRLFilterDto.class;
    }
}
