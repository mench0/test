package com.tms.profilemanager.app.mapper.filter;

import com.querydsl.core.types.Predicate;
import com.tms.profilemanager.app.config.security.AuthExpressionsHolder;
import com.tms.profilemanager.app.mapper.filter.utils.PredicateBuilder;
import com.tms.profilemanager.dto.enums.sort.ContactLimitSort;
import com.tms.profilemanager.dto.filter.ContactLimitFilterDto;
import com.tms.profilemanager.dto.filter.SearchRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import static com.tms.profilemanager.app.entity.QContactLimit.contactLimit;
import static com.tms.profilemanager.app.mapper.filter.utils.SortBuilder.buildOrder;
import static com.tms.profilemanager.dto.enums.Permission.PAYMENT_SYSTEM_READ;

@Component
public class ContactLimitSearchMapper extends SearchMapper<ContactLimitFilterDto, ContactLimitSort> {

    @Override
    protected Predicate toPredicateExceptSubFilters(ContactLimitFilterDto filter) {
        return PredicateBuilder.builder()
                .in(contactLimit.id, filter.getIds())
                .eq(contactLimit.archived, filter.getArchived())
                .build();
    }

    @Override
    public Predicate getAccessControlPredicate() {
        return AuthExpressionsHolder.hasPermission(PAYMENT_SYSTEM_READ) ? null : PredicateBuilder.buildAlwaysFalsePredicate();
    }

    @Override
    protected Sort.Order toOrder(ContactLimitSort sortName, SearchRequest.SortDirection direction) {
        return switch (sortName) {
            case CREATED_AT -> buildOrder(contactLimit.createdAt, direction);
        };
    }

    @Override
    protected Class<ContactLimitFilterDto> getFilterDtoClass() {
        return ContactLimitFilterDto.class;
    }
}
