package com.tms.profilemanager.app.mapper.filter;

import com.querydsl.core.types.Predicate;
import com.tms.profilemanager.app.mapper.filter.utils.PredicateBuilder;
import com.tms.profilemanager.dto.enums.sort.CardCodeSort;
import com.tms.profilemanager.dto.filter.CardCodeFilterDto;
import com.tms.profilemanager.dto.filter.SearchRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import static com.tms.profilemanager.app.config.security.AuthExpressionsHolder.hasPermission;
import static com.tms.profilemanager.app.entity.dictionary.QCardCode.cardCode;
import static com.tms.profilemanager.app.mapper.filter.utils.SortBuilder.buildOrder;
import static com.tms.profilemanager.dto.enums.Permission.TERMINAL_READ;

@Component
public class CardCodeSearchMapper extends SearchMapper<CardCodeFilterDto, CardCodeSort> {
    @Override
    protected Predicate toPredicateExceptSubFilters(CardCodeFilterDto filter) {
        return PredicateBuilder.builder()
                .in(cardCode.id, filter.getIds())
                .in(cardCode.cardTypeId, filter.getCardTypeIds())
                .build();
    }

    @Override
    public Predicate getAccessControlPredicate() {
        return hasPermission(TERMINAL_READ) ? null : PredicateBuilder.buildAlwaysFalsePredicate();
    }

    @Override
    protected Sort.Order toOrder(CardCodeSort sortName, SearchRequest.SortDirection direction) {
        return switch (sortName) {
            case CREATED_AT -> buildOrder(cardCode.createdAt, direction);
        };
    }

    @Override
    protected Class<CardCodeFilterDto> getFilterDtoClass() {
        return CardCodeFilterDto.class;
    }
}
