package com.tms.profilemanager.app.mapper.filter;

import com.querydsl.core.types.Predicate;
import com.tms.profilemanager.app.mapper.filter.utils.PredicateBuilder;
import com.tms.profilemanager.dto.enums.sort.ModificationTerminalSort;
import com.tms.profilemanager.dto.filter.ModificationTerminalFilterDto;
import com.tms.profilemanager.dto.filter.SearchRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import static com.tms.profilemanager.app.config.security.AuthExpressionsHolder.hasPermission;
import static com.tms.profilemanager.app.entity.modification.QModificationTerminal.modificationTerminal;
import static com.tms.profilemanager.app.mapper.filter.utils.SortBuilder.buildOrder;
import static com.tms.profilemanager.dto.enums.Permission.TERMINAL_GROUP_READ;

@Component
public class ModificationTerminalSearchMapper extends SearchMapper<ModificationTerminalFilterDto, ModificationTerminalSort> {
    @Override
    protected Predicate toPredicateExceptSubFilters(ModificationTerminalFilterDto filter) {
        return PredicateBuilder.builder()
                .in(modificationTerminal.id, filter.getIds())
                .in(modificationTerminal.groupId, filter.getGroupIds())
                .build();
    }

    @Override
    public Predicate getAccessControlPredicate() {
        return hasPermission(TERMINAL_GROUP_READ) ? null : PredicateBuilder.buildAlwaysFalsePredicate();
    }

    @Override
    protected Sort.Order toOrder(ModificationTerminalSort sortName, SearchRequest.SortDirection direction) {
        return switch (sortName) {
            case CREATED_AT -> buildOrder(modificationTerminal.createdAt, direction);
        };
    }

    @Override
    protected Class<ModificationTerminalFilterDto> getFilterDtoClass() {
        return ModificationTerminalFilterDto.class;
    }
}
