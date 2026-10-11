package com.tms.profilemanager.app.mapper.filter;


import com.querydsl.core.types.Predicate;
import com.tms.profilemanager.app.mapper.filter.utils.PredicateBuilder;
import com.tms.profilemanager.dto.enums.sort.PaymentTypeSort;
import com.tms.profilemanager.dto.filter.PaymentTypeFilterDto;
import com.tms.profilemanager.dto.filter.SearchRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import static com.tms.profilemanager.app.config.security.AuthExpressionsHolder.hasPermission;
import static com.tms.profilemanager.app.entity.QPaymentType.paymentType;
import static com.tms.profilemanager.app.mapper.filter.utils.SortBuilder.buildOrder;
import static com.tms.profilemanager.dto.enums.Permission.PAYMENT_TYPE_READ;

@Component
public class PaymentTypeSearchMapper extends SearchMapper<PaymentTypeFilterDto, PaymentTypeSort> {

    @Override
    protected Predicate toPredicateExceptSubFilters(PaymentTypeFilterDto filter) {
        return PredicateBuilder.builder()
                .in(paymentType.id, filter.getIds())
                .build();
    }

    @Override
    public Predicate getAccessControlPredicate() {
        return hasPermission(PAYMENT_TYPE_READ) ? null : PredicateBuilder.buildAlwaysFalsePredicate();
    }

    @Override
    protected Sort.Order toOrder(PaymentTypeSort sortName, SearchRequest.SortDirection direction) {
        return switch (sortName) {
            case CREATED_AT -> buildOrder(paymentType.createdAt, direction);
        };
    }

    @Override
    protected Class<PaymentTypeFilterDto> getFilterDtoClass() {
        return PaymentTypeFilterDto.class;
    }
}
