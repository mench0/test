package com.tms.profilemanager.app.mapper.filter;

import com.querydsl.core.types.Predicate;
import com.tms.profilemanager.app.config.security.AuthExpressionsHolder;
import com.tms.profilemanager.app.mapper.filter.utils.PredicateBuilder;
import com.tms.profilemanager.dto.enums.sort.ContactlessLimitSort;
import com.tms.profilemanager.dto.filter.ContactlessLimitFilterDto;
import com.tms.profilemanager.dto.filter.SearchRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import static com.tms.profilemanager.app.entity.QContactlessLimit.contactlessLimit;
import static com.tms.profilemanager.app.mapper.filter.utils.SortBuilder.buildOrder;
import static com.tms.profilemanager.dto.enums.Permission.PAYMENT_SYSTEM_READ;

@Component
public class ContactlessLimitSearchMapper extends SearchMapper<ContactlessLimitFilterDto, ContactlessLimitSort> {

    @Override
    protected Predicate toPredicateExceptSubFilters(ContactlessLimitFilterDto filter) {
        return PredicateBuilder.builder()
                .in(contactlessLimit.id, filter.getIds())
                .eq(contactlessLimit.archived, filter.getArchived())
                .build();
    }

    @Override
    public Predicate getAccessControlPredicate() {
        return AuthExpressionsHolder.hasPermission(PAYMENT_SYSTEM_READ) ? null : PredicateBuilder.buildAlwaysFalsePredicate();
    }

    @Override
    protected Sort.Order toOrder(ContactlessLimitSort sortName, SearchRequest.SortDirection direction) {
        return switch (sortName) {
            case CREATED_AT -> buildOrder(contactlessLimit.createdAt, direction);
        };
    }

    @Override
    protected Class<ContactlessLimitFilterDto> getFilterDtoClass() {
        return ContactlessLimitFilterDto.class;
    }
}
