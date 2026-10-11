package com.tms.profilemanager.app.mapper.filter;

import com.querydsl.core.types.Predicate;
import com.tms.profilemanager.app.config.security.AuthExpressionsHolder;
import com.tms.profilemanager.app.mapper.filter.utils.PredicateBuilder;
import com.tms.profilemanager.dto.enums.sort.EMVAppSort;
import com.tms.profilemanager.dto.filter.EMVAppFilterDto;
import com.tms.profilemanager.dto.filter.SearchRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import static com.tms.profilemanager.app.entity.QEMVApp.eMVApp;
import static com.tms.profilemanager.app.mapper.filter.utils.SortBuilder.buildOrder;
import static com.tms.profilemanager.dto.enums.Permission.PAYMENT_SYSTEM_READ;

@Component
public class EMVAppSearchMapper extends SearchMapper<EMVAppFilterDto, EMVAppSort> {

    @Override
    protected Predicate toPredicateExceptSubFilters(EMVAppFilterDto filter) {
        return PredicateBuilder.builder()
                .in(eMVApp.id, filter.getIds())
                .eq(eMVApp.archived, filter.getArchived())
                .in(eMVApp.paymentSystemId, filter.getPaymentSystemIds())
                .build();
    }

    @Override
    public Predicate getAccessControlPredicate() {
        return AuthExpressionsHolder.hasPermission(PAYMENT_SYSTEM_READ) ? null : PredicateBuilder.buildAlwaysFalsePredicate();
    }

    @Override
    protected Sort.Order toOrder(EMVAppSort sortName, SearchRequest.SortDirection direction) {
        return switch (sortName) {
            case CREATED_AT -> buildOrder(eMVApp.createdAt, direction);
        };
    }

    @Override
    protected Class<EMVAppFilterDto> getFilterDtoClass() {
        return EMVAppFilterDto.class;
    }
}
