package com.tms.profilemanager.app.mapper.filter;

import com.querydsl.core.types.Predicate;
import com.tms.profilemanager.app.mapper.filter.utils.PredicateBuilder;
import com.tms.profilemanager.dto.enums.sort.ModificationPaymentSystemSort;
import com.tms.profilemanager.dto.filter.ModificationPaymentSystemFilterDto;
import com.tms.profilemanager.dto.filter.SearchRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import static com.tms.profilemanager.app.config.security.AuthExpressionsHolder.hasPermission;
import static com.tms.profilemanager.app.entity.modification.QModificationPaymentSystem.modificationPaymentSystem;
import static com.tms.profilemanager.app.mapper.filter.utils.SortBuilder.buildOrder;
import static com.tms.profilemanager.dto.enums.Permission.TERMINAL_GROUP_READ;

@Component
public class ModificationPaymentSystemSearchMapper extends SearchMapper<ModificationPaymentSystemFilterDto, ModificationPaymentSystemSort> {
    @Override
    protected Predicate toPredicateExceptSubFilters(ModificationPaymentSystemFilterDto filter) {
        return PredicateBuilder.builder()
                .in(modificationPaymentSystem.id, filter.getIds())
                .in(modificationPaymentSystem.groupId, filter.getGroupIds())
                .build();
    }

    @Override
    public Predicate getAccessControlPredicate() {
        return hasPermission(TERMINAL_GROUP_READ) ? null : PredicateBuilder.buildAlwaysFalsePredicate();
    }

    @Override
    protected Sort.Order toOrder(ModificationPaymentSystemSort sortName, SearchRequest.SortDirection direction) {
        return switch (sortName) {
            case CREATED_AT -> buildOrder(modificationPaymentSystem.createdAt, direction);
        };
    }

    @Override
    protected Class<ModificationPaymentSystemFilterDto> getFilterDtoClass() {
        return ModificationPaymentSystemFilterDto.class;
    }
}
