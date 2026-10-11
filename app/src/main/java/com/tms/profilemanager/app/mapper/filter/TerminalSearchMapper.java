package com.tms.profilemanager.app.mapper.filter;

import com.querydsl.core.types.Predicate;
import com.tms.profilemanager.app.config.security.CurrentUserInfo;
import com.tms.profilemanager.app.mapper.filter.utils.PredicateBuilder;
import com.tms.profilemanager.app.utils.AuthenticationHolder;
import com.tms.profilemanager.dto.enums.sort.TerminalSort;
import com.tms.profilemanager.dto.filter.SearchRequest;
import com.tms.profilemanager.dto.filter.TerminalFilterDto;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import static com.tms.profilemanager.app.config.security.AuthExpressionsHolder.hasPermission;
import static com.tms.profilemanager.app.entity.QTerminal.terminal;
import static com.tms.profilemanager.app.mapper.filter.utils.SortBuilder.buildOrder;
import static com.tms.profilemanager.dto.enums.Permission.TERMINAL_READ;

@Component
public class TerminalSearchMapper extends SearchMapper<TerminalFilterDto, TerminalSort> {

    @Override
    protected Predicate toPredicateExceptSubFilters(TerminalFilterDto filter) {
        return PredicateBuilder.builder()
                .in(terminal.id, filter.getIds())
                .eq(terminal.archived, filter.getArchived())
                .in(terminal.group.id, filter.getTerminalGroupIds())
                .in(terminal.merchant.id, filter.getMerchantIds())
                .containsIgnoreCase(terminal.serialNumber, filter.getSearchSerialNumber())
                .containsIgnoreCase(terminal.terminalAcquirerParams.any().isoTerminalId, filter.getSearchTid())
                .containsIgnoreCase(terminal.terminalAcquirerParams.any().isoMerchantId, filter.getSearchMid())
                .build();
    }

    @Override
    public Predicate getAccessControlPredicate() {
        if (hasPermission(TERMINAL_READ)) {
            return null;
        }
        String terminalSerialNumber = AuthenticationHolder.getCurrentUserInfo()
                .map(CurrentUserInfo::getTerminalSerialNumber)
                .orElse(null);
        if (terminalSerialNumber != null) {
            return PredicateBuilder.builder().eq(terminal.serialNumber, terminalSerialNumber).build();
        }
        return PredicateBuilder.buildAlwaysFalsePredicate();
    }

    @Override
    protected Sort.Order toOrder(TerminalSort sortName, SearchRequest.SortDirection direction) {
        return switch (sortName) {
            case CREATED_AT -> buildOrder(terminal.createdAt, direction);
        };
    }

    @Override
    protected Class<TerminalFilterDto> getFilterDtoClass() {
        return TerminalFilterDto.class;
    }
}
