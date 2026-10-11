package com.tms.profilemanager.app.mapper.filter;

import com.querydsl.core.types.Predicate;
import com.tms.profilemanager.app.mapper.filter.utils.PredicateBuilder;
import com.tms.profilemanager.dto.enums.sort.ModificationEmvAppSort;
import com.tms.profilemanager.dto.filter.ModificationEmvAppFilterDto;
import com.tms.profilemanager.dto.filter.SearchRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import static com.tms.profilemanager.app.config.security.AuthExpressionsHolder.hasPermission;
import static com.tms.profilemanager.app.entity.modification.QModificationEmvApp.modificationEmvApp;
import static com.tms.profilemanager.app.entity.modification.QModificationPaymentSystem.modificationPaymentSystem;
import static com.tms.profilemanager.app.mapper.filter.utils.SortBuilder.buildOrder;
import static com.tms.profilemanager.dto.enums.Permission.TERMINAL_GROUP_READ;

@Component
public class ModificationEmvAppSearchMapper extends SearchMapper<ModificationEmvAppFilterDto, ModificationEmvAppSort> {
    @Override
    protected Predicate toPredicateExceptSubFilters(ModificationEmvAppFilterDto filter) {
        return PredicateBuilder.builder()
                .in(modificationEmvApp.id, filter.getIds())
                .in(modificationEmvApp.modificationEmvAppsPaymentSystem.id, filter.getModificationIds())
                .build();
    }

    @Override
    public Predicate getAccessControlPredicate() {
        return hasPermission(TERMINAL_GROUP_READ) ? null : PredicateBuilder.buildAlwaysFalsePredicate();
    }

    @Override
    protected Sort.Order toOrder(ModificationEmvAppSort sortName, SearchRequest.SortDirection direction) {
        return switch (sortName) {
            case CREATED_AT -> buildOrder(modificationPaymentSystem.createdAt, direction);
        };
    }

    @Override
    protected Class<ModificationEmvAppFilterDto> getFilterDtoClass() {
        return ModificationEmvAppFilterDto.class;
    }
}
