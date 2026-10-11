package com.tms.profilemanager.app.mapper.filter;

import com.querydsl.core.types.Predicate;
import com.tms.profilemanager.app.mapper.filter.utils.PredicateBuilder;
import com.tms.profilemanager.dto.enums.sort.ModificationAcquirerSort;
import com.tms.profilemanager.dto.filter.ModificationAcquirerFilterDto;
import com.tms.profilemanager.dto.filter.SearchRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import static com.tms.profilemanager.app.config.security.AuthExpressionsHolder.hasPermission;
import static com.tms.profilemanager.app.entity.modification.QModificationAcquirer.modificationAcquirer;
import static com.tms.profilemanager.app.mapper.filter.utils.SortBuilder.buildOrder;
import static com.tms.profilemanager.dto.enums.Permission.TERMINAL_GROUP_READ;

@Component
public class ModificationAcquirerSearchMapper extends SearchMapper<ModificationAcquirerFilterDto, ModificationAcquirerSort> {
    @Override
    protected Predicate toPredicateExceptSubFilters(ModificationAcquirerFilterDto filter) {
        return PredicateBuilder.builder()
                .in(modificationAcquirer.id, filter.getIds())
                .in(modificationAcquirer.groupId, filter.getGroupIds())
                .build();
    }

    @Override
    public Predicate getAccessControlPredicate() {
        return hasPermission(TERMINAL_GROUP_READ) ? null : PredicateBuilder.buildAlwaysFalsePredicate();
    }

    @Override
    protected Sort.Order toOrder(ModificationAcquirerSort sortName, SearchRequest.SortDirection direction) {
        return switch (sortName) {
            case CREATED_AT -> buildOrder(modificationAcquirer.createdAt, direction);
        };
    }

    @Override
    protected Class<ModificationAcquirerFilterDto> getFilterDtoClass() {
        return ModificationAcquirerFilterDto.class;
    }
}
