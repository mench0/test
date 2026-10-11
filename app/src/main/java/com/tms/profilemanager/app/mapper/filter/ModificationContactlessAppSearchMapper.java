package com.tms.profilemanager.app.mapper.filter;

import com.querydsl.core.types.Predicate;
import com.tms.profilemanager.app.mapper.filter.utils.PredicateBuilder;
import com.tms.profilemanager.dto.enums.sort.ModificationContactlessAppSort;
import com.tms.profilemanager.dto.filter.ModificationContactlessAppFilterDto;
import com.tms.profilemanager.dto.filter.SearchRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import static com.tms.profilemanager.app.config.security.AuthExpressionsHolder.hasPermission;
import static com.tms.profilemanager.app.entity.modification.QModificationContactlessApp.modificationContactlessApp;
import static com.tms.profilemanager.app.entity.modification.QModificationPaymentSystem.modificationPaymentSystem;
import static com.tms.profilemanager.app.mapper.filter.utils.SortBuilder.buildOrder;
import static com.tms.profilemanager.dto.enums.Permission.TERMINAL_GROUP_READ;

@Component
public class ModificationContactlessAppSearchMapper extends SearchMapper<ModificationContactlessAppFilterDto, ModificationContactlessAppSort> {
    @Override
    protected Predicate toPredicateExceptSubFilters(ModificationContactlessAppFilterDto filter) {
        return PredicateBuilder.builder()
                .in(modificationContactlessApp.id, filter.getIds())
                .in(modificationContactlessApp.modificationContactlessAppsPaymentSystem.id, filter.getModificationIds())
                .build();
    }

    @Override
    public Predicate getAccessControlPredicate() {
        return hasPermission(TERMINAL_GROUP_READ) ? null : PredicateBuilder.buildAlwaysFalsePredicate();
    }

    @Override
    protected Sort.Order toOrder(ModificationContactlessAppSort sortName, SearchRequest.SortDirection direction) {
        return switch (sortName) {
            case CREATED_AT -> buildOrder(modificationPaymentSystem.createdAt, direction);
        };
    }

    @Override
    protected Class<ModificationContactlessAppFilterDto> getFilterDtoClass() {
        return ModificationContactlessAppFilterDto.class;
    }
}
