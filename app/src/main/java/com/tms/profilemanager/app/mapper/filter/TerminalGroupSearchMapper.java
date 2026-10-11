package com.tms.profilemanager.app.mapper.filter;

import com.querydsl.core.types.Predicate;
import com.tms.profilemanager.app.mapper.filter.utils.PredicateBuilder;
import com.tms.profilemanager.dto.enums.sort.TerminalGroupSort;
import com.tms.profilemanager.dto.filter.SearchRequest;
import com.tms.profilemanager.dto.filter.TerminalGroupFilterDto;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import static com.tms.profilemanager.app.config.security.AuthExpressionsHolder.hasPermission;
import static com.tms.profilemanager.app.entity.QTerminalGroup.terminalGroup;
import static com.tms.profilemanager.app.mapper.filter.utils.SortBuilder.buildOrder;
import static com.tms.profilemanager.dto.enums.Permission.TERMINAL_GROUP_READ;

@Component
public class TerminalGroupSearchMapper extends SearchMapper<TerminalGroupFilterDto, TerminalGroupSort> {

    @Override
    protected Predicate toPredicateExceptSubFilters(TerminalGroupFilterDto filter) {
        return PredicateBuilder.builder()
                .in(terminalGroup.id, filter.getIds())
                .eq(terminalGroup.archived, filter.getArchived())
                .build();
    }

    @Override
    public Predicate getAccessControlPredicate() {
        return hasPermission(TERMINAL_GROUP_READ) ? null : PredicateBuilder.buildAlwaysFalsePredicate();
    }

    @Override
    protected Sort.Order toOrder(TerminalGroupSort sortName, SearchRequest.SortDirection direction) {
        return switch (sortName) {
            case CREATED_AT -> buildOrder(terminalGroup.createdAt, direction);
            case NAME -> buildOrder(terminalGroup.name, direction);
        };
    }

    @Override
    protected Class<TerminalGroupFilterDto> getFilterDtoClass() {
        return TerminalGroupFilterDto.class;
    }
}
