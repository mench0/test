package com.tms.profilemanager.app.mapper.filter;

import com.querydsl.core.types.Predicate;
import com.tms.profilemanager.app.config.security.AuthExpressionsHolder;
import com.tms.profilemanager.app.mapper.filter.utils.PredicateBuilder;
import com.tms.profilemanager.dto.enums.sort.CardRangeSort;
import com.tms.profilemanager.dto.filter.CardRangeFilterDto;
import com.tms.profilemanager.dto.filter.SearchRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import static com.tms.profilemanager.app.entity.QCardRange.cardRange;
import static com.tms.profilemanager.app.mapper.filter.utils.SortBuilder.buildOrder;
import static com.tms.profilemanager.dto.enums.Permission.PAYMENT_SYSTEM_READ;

@Component
public class CardRangeSearchMapper extends SearchMapper<CardRangeFilterDto, CardRangeSort> {

    @Override
    protected Predicate toPredicateExceptSubFilters(CardRangeFilterDto filter) {
        return PredicateBuilder.builder()
                .in(cardRange.id, filter.getIds())
                .eq(cardRange.archived, filter.getArchived())
                .in(cardRange.acquirerId, filter.getAcquirerIds())
                .build();
    }

    @Override
    public Predicate getAccessControlPredicate() {
        return AuthExpressionsHolder.hasPermission(PAYMENT_SYSTEM_READ) ? null : PredicateBuilder.buildAlwaysFalsePredicate();
    }

    @Override
    protected Sort.Order toOrder(CardRangeSort sortName, SearchRequest.SortDirection direction) {
        return switch (sortName) {
            case CREATED_AT -> buildOrder(cardRange.createdAt, direction);
        };
    }

    @Override
    protected Class<CardRangeFilterDto> getFilterDtoClass() {
        return CardRangeFilterDto.class;
    }
}
