package com.tms.profilemanager.app.mapper.filter;

import com.querydsl.core.types.Predicate;
import com.tms.profilemanager.app.mapper.filter.utils.PredicateBuilder;
import com.tms.profilemanager.dto.enums.sort.CardTypeSort;
import com.tms.profilemanager.dto.filter.CardTypeFilterDto;
import com.tms.profilemanager.dto.filter.SearchRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import static com.tms.profilemanager.app.config.security.AuthExpressionsHolder.hasPermission;
import static com.tms.profilemanager.app.entity.dictionary.QCardType.cardType;
import static com.tms.profilemanager.app.mapper.filter.utils.SortBuilder.buildOrder;
import static com.tms.profilemanager.dto.enums.Permission.TERMINAL_READ;

@Component
public class CardTypeSearchMapper extends SearchMapper<CardTypeFilterDto, CardTypeSort> {
    @Override
    protected Predicate toPredicateExceptSubFilters(CardTypeFilterDto filter) {
        return PredicateBuilder.builder()
                .in(cardType.id, filter.getIds())
                .build();
    }

    @Override
    public Predicate getAccessControlPredicate() {
        return hasPermission(TERMINAL_READ) ? null : PredicateBuilder.buildAlwaysFalsePredicate();
    }

    @Override
    protected Sort.Order toOrder(CardTypeSort sortName, SearchRequest.SortDirection direction) {
        return switch (sortName) {
            case CREATED_AT -> buildOrder(cardType.createdAt, direction);
        };
    }

    @Override
    protected Class<CardTypeFilterDto> getFilterDtoClass() {
        return CardTypeFilterDto.class;
    }
}
