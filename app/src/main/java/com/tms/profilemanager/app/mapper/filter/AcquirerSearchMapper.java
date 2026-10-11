package com.tms.profilemanager.app.mapper.filter;

import com.querydsl.core.types.Predicate;
import com.querydsl.jpa.JPAExpressions;
import com.tms.profilemanager.app.mapper.filter.utils.PredicateBuilder;
import com.tms.profilemanager.dto.enums.sort.AcquirerSort;
import com.tms.profilemanager.dto.filter.AcquirerFilterDto;
import com.tms.profilemanager.dto.filter.SearchRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import static com.tms.profilemanager.app.config.security.AuthExpressionsHolder.hasPermission;
import static com.tms.profilemanager.app.entity.QAcquirer.acquirer;
import static com.tms.profilemanager.app.entity.QTerminal.terminal;
import static com.tms.profilemanager.app.mapper.filter.utils.SortBuilder.buildOrder;
import static com.tms.profilemanager.dto.enums.Permission.ACQUIRER_READ;
import static org.apache.commons.collections4.CollectionUtils.isNotEmpty;

@Component
public class AcquirerSearchMapper extends SearchMapper<AcquirerFilterDto, AcquirerSort> {

    @Override
    protected Predicate toPredicateExceptSubFilters(AcquirerFilterDto filter) {
        PredicateBuilder builder = PredicateBuilder.builder()
                .in(acquirer.id, filter.getIds())
                .eq(acquirer.archived, filter.getArchived())
                .in(acquirer.name, filter.getNames());
        if (isNotEmpty(filter.getTerminalsIds())) {
                builder.and(JPAExpressions.selectFrom(terminal)
                    .where(PredicateBuilder.builder()
                            .in(terminal.id, filter.getTerminalsIds())
                            .and(terminal.terminalAcquirerParams.any().acquirerId.eq(acquirer.id))
                            .build())
                    .exists());
        }
        return builder.build();
    }

    @Override
    public Predicate getAccessControlPredicate() {
        return hasPermission(ACQUIRER_READ) ? null : PredicateBuilder.buildAlwaysFalsePredicate();
    }

    @Override
    protected Sort.Order toOrder(AcquirerSort sortName, SearchRequest.SortDirection direction) {
        return switch (sortName) {
            case NAME -> buildOrder(acquirer.name, direction);
            case CREATED_AT -> buildOrder(acquirer.createdAt, direction);
        };
    }

    @Override
    protected Class<AcquirerFilterDto> getFilterDtoClass() {
        return AcquirerFilterDto.class;
    }
}
