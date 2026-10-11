package com.tms.profilemanager.app.mapper.filter;

import com.querydsl.core.types.Predicate;
import com.tms.profilemanager.app.mapper.filter.utils.PredicateBuilder;
import com.tms.profilemanager.dto.enums.sort.CardBinRangeSort;
import com.tms.profilemanager.dto.filter.CardBinRangeFilterDto;
import com.tms.profilemanager.dto.filter.SearchRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import static com.tms.profilemanager.app.config.security.AuthExpressionsHolder.hasPermission;
import static com.tms.profilemanager.app.entity.dictionary.QCardBinRange.cardBinRange;
import static com.tms.profilemanager.app.mapper.filter.utils.SortBuilder.buildOrder;
import static com.tms.profilemanager.dto.enums.Permission.TERMINAL_READ;

@Component
public class CardBinRangeSearchMapper extends SearchMapper<CardBinRangeFilterDto, CardBinRangeSort> {
    @Override
    protected Predicate toPredicateExceptSubFilters(CardBinRangeFilterDto filter) {
        return PredicateBuilder.builder()
                .in(cardBinRange.id, filter.getIds())
                .build();
    }

    @Override
    public Predicate getAccessControlPredicate() {
        return hasPermission(TERMINAL_READ) ? null : PredicateBuilder.buildAlwaysFalsePredicate();
    }

    @Override
    protected Sort.Order toOrder(CardBinRangeSort sortName, SearchRequest.SortDirection direction) {
        return switch (sortName) {
            case CREATED_AT -> buildOrder(cardBinRange.createdAt, direction);
        };
    }

    @Override
    protected Class<CardBinRangeFilterDto> getFilterDtoClass() {
        return CardBinRangeFilterDto.class;
    }
}
