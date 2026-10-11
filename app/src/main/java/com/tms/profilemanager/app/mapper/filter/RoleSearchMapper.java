package com.tms.profilemanager.app.mapper.filter;

import com.querydsl.core.types.Predicate;
import com.tms.profilemanager.app.config.security.AuthExpressionsHolder;
import com.tms.profilemanager.app.mapper.filter.utils.PredicateBuilder;
import com.tms.profilemanager.app.utils.AuthenticationHolder;
import com.tms.profilemanager.dto.enums.Permission;
import com.tms.profilemanager.dto.enums.sort.RoleSort;
import com.tms.profilemanager.dto.filter.RoleFilterDto;
import com.tms.profilemanager.dto.filter.SearchRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import static com.tms.profilemanager.app.entity.QRole.role;
import static com.tms.profilemanager.app.mapper.filter.utils.SortBuilder.buildOrder;

@Component
public class RoleSearchMapper extends SearchMapper<RoleFilterDto, RoleSort> {

    @Override
    protected Predicate toPredicateExceptSubFilters(RoleFilterDto filter) {
        return PredicateBuilder.builder()
                .in(role.id, filter.getIds())
                .eq(role.archived, filter.getArchived())
                .eq(role.systemUserRole, filter.getSystemUserRole())
                .in(role.name, filter.getNames())
                .build();
    }

    @Override
    public Predicate getAccessControlPredicate() {
        if (!AuthExpressionsHolder.hasPermission(Permission.ROLE_READ)) {
            PredicateBuilder.builder()
                    .eq(role.id, AuthenticationHolder.getCurrentUserInfoOrElseThrow().getRoleId())
                    .build();
        }
        return null;
    }

    @Override
    protected Sort.Order toOrder(RoleSort sortName, SearchRequest.SortDirection direction) {
        return switch (sortName) {
            case CREATED_AT -> buildOrder(role.createdAt, direction);
            case NAME -> buildOrder(role.name, direction);
        };
    }

    @Override
    protected Class<RoleFilterDto> getFilterDtoClass() {
        return RoleFilterDto.class;
    }
}
