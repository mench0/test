package com.tms.profilemanager.app.mapper.filter;

import com.querydsl.core.types.Predicate;
import com.tms.profilemanager.app.config.security.AuthExpressionsHolder;
import com.tms.profilemanager.app.mapper.filter.utils.PredicateBuilder;
import com.tms.profilemanager.dto.enums.sort.ContactlessAppSort;
import com.tms.profilemanager.dto.filter.ContactlessAppFilterDto;
import com.tms.profilemanager.dto.filter.SearchRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import static com.tms.profilemanager.app.entity.QContactlessApp.contactlessApp;
import static com.tms.profilemanager.app.mapper.filter.utils.SortBuilder.buildOrder;
import static com.tms.profilemanager.dto.enums.Permission.PAYMENT_SYSTEM_READ;

@Component
public class ContactlessAppSearchMapper extends SearchMapper<ContactlessAppFilterDto, ContactlessAppSort> {

    @Override
    protected Predicate toPredicateExceptSubFilters(ContactlessAppFilterDto filter) {
        return PredicateBuilder.builder()
                .in(contactlessApp.id, filter.getIds())
                .eq(contactlessApp.archived, filter.getArchived())
                .in(contactlessApp.paymentSystemId, filter.getPaymentSystemIds())
                .build();
    }

    @Override
    public Predicate getAccessControlPredicate() {
        return AuthExpressionsHolder.hasPermission(PAYMENT_SYSTEM_READ) ? null : PredicateBuilder.buildAlwaysFalsePredicate();
    }

    @Override
    protected Sort.Order toOrder(ContactlessAppSort sortName, SearchRequest.SortDirection direction) {
        return switch (sortName) {
            case CREATED_AT -> buildOrder(contactlessApp.createdAt, direction);
        };
    }

    @Override
    protected Class<ContactlessAppFilterDto> getFilterDtoClass() {
        return ContactlessAppFilterDto.class;
    }
}
