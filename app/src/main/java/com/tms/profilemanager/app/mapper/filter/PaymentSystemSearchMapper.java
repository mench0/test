package com.tms.profilemanager.app.mapper.filter;

import com.querydsl.core.types.Predicate;
import com.querydsl.jpa.JPAExpressions;
import com.tms.profilemanager.app.config.security.AuthExpressionsHolder;
import com.tms.profilemanager.app.mapper.filter.utils.PredicateBuilder;
import com.tms.profilemanager.dto.enums.sort.PaymentSystemSort;
import com.tms.profilemanager.dto.filter.PaymentSystemFilterDto;
import com.tms.profilemanager.dto.filter.SearchRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import static com.tms.profilemanager.app.entity.QAcquirer.acquirer;
import static com.tms.profilemanager.app.entity.QPaymentSystem.paymentSystem;
import static com.tms.profilemanager.app.mapper.filter.utils.SortBuilder.buildOrder;
import static com.tms.profilemanager.dto.enums.Permission.PAYMENT_SYSTEM_READ;
import static org.apache.commons.collections4.CollectionUtils.isNotEmpty;

@Component
public class PaymentSystemSearchMapper extends SearchMapper<PaymentSystemFilterDto, PaymentSystemSort> {

    @Override
    protected Predicate toPredicateExceptSubFilters(PaymentSystemFilterDto filter) {
        PredicateBuilder builder = PredicateBuilder.builder()
                .in(paymentSystem.id, filter.getIds())
                .eq(paymentSystem.archived, filter.getArchived())
                .in(paymentSystem.name, filter.getNames());

        if (isNotEmpty(filter.getAcquirerIds())) {
            builder.and(JPAExpressions.selectFrom(acquirer)
                    .where(PredicateBuilder.builder()
                            .in(acquirer.id, filter.getAcquirerIds())
                            .and(acquirer.paymentSystemIds.any().eq(paymentSystem.id))
                            .build())
                    .exists());
        }

        return builder.build();
    }

    @Override
    public Predicate getAccessControlPredicate() {
        return AuthExpressionsHolder.hasPermission(PAYMENT_SYSTEM_READ) ? null : PredicateBuilder.buildAlwaysFalsePredicate();
    }

    @Override
    protected Sort.Order toOrder(PaymentSystemSort sortName, SearchRequest.SortDirection direction) {
        return switch (sortName) {
            case CREATED_AT -> buildOrder(paymentSystem.createdAt, direction);
            case NAME -> buildOrder(paymentSystem.name, direction);
        };
    }

    @Override
    protected Class<PaymentSystemFilterDto> getFilterDtoClass() {
        return PaymentSystemFilterDto.class;
    }
}
