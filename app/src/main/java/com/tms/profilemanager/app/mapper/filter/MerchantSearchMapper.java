package com.tms.profilemanager.app.mapper.filter;

import com.querydsl.core.types.Predicate;
import com.tms.profilemanager.app.mapper.filter.utils.PredicateBuilder;
import com.tms.profilemanager.dto.enums.sort.MerchantSort;
import com.tms.profilemanager.dto.filter.MerchantFilterDto;
import com.tms.profilemanager.dto.filter.SearchRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import static com.tms.profilemanager.app.config.security.AuthExpressionsHolder.hasPermission;
import static com.tms.profilemanager.app.entity.QMerchant.merchant;
import static com.tms.profilemanager.app.mapper.filter.utils.SortBuilder.buildOrder;
import static com.tms.profilemanager.dto.enums.Permission.MERCHANT_READ;

@Component
public class MerchantSearchMapper extends SearchMapper<MerchantFilterDto, MerchantSort> {

    @Override
    protected Predicate toPredicateExceptSubFilters(MerchantFilterDto filter) {
        return PredicateBuilder.builder()
                .in(merchant.id, filter.getIds())
                .eq(merchant.archived, filter.getArchived())
                .containsIgnoreCase(merchant.name, filter.getSearchText())
                .build();
    }

    @Override
    public Predicate getAccessControlPredicate() {
        return hasPermission(MERCHANT_READ) ? null : PredicateBuilder.buildAlwaysFalsePredicate();
    }

    @Override
    protected Sort.Order toOrder(MerchantSort sortName, SearchRequest.SortDirection direction) {
        return switch (sortName) {
            case CREATED_AT -> buildOrder(merchant.createdAt, direction);
            case NAME -> buildOrder(merchant.name, direction);
        };
    }

    @Override
    protected Class<MerchantFilterDto> getFilterDtoClass() {
        return MerchantFilterDto.class;
    }
}
