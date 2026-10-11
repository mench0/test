package com.tms.profilemanager.app.mapper.filter;

import com.querydsl.core.types.Predicate;
import com.tms.profilemanager.app.config.security.AuthExpressionsHolder;
import com.tms.profilemanager.app.entity.QSystemUser;
import com.tms.profilemanager.app.entity.QUsualUser;
import com.tms.profilemanager.app.entity.SystemUser;
import com.tms.profilemanager.app.entity.UsualUser;
import com.tms.profilemanager.app.mapper.filter.utils.PredicateBuilder;
import com.tms.profilemanager.app.utils.AuthenticationHolder;
import com.tms.profilemanager.dto.enums.Permission;
import com.tms.profilemanager.dto.enums.sort.UserSort;
import com.tms.profilemanager.dto.filter.SearchRequest;
import com.tms.profilemanager.dto.filter.UserFilterDto;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import static com.tms.profilemanager.app.entity.QUser.user;
import static com.tms.profilemanager.app.mapper.filter.utils.SortBuilder.buildOrder;
import static org.apache.commons.collections4.CollectionUtils.isNotEmpty;

@Component
public class UserSearchMapper extends SearchMapper<UserFilterDto, UserSort> {

    @Override
    protected Predicate toPredicateExceptSubFilters(UserFilterDto filter) {
        PredicateBuilder builder = PredicateBuilder.builder()
                .in(user.id, filter.getIds())
                .eq(user.archived, filter.getArchived())
                .in(user.username, filter.getUsernames())
                .in(user.as(QSystemUser.class).terminalSerialNumber, filter.getTerminalSerialNumbers());
        if (isNotEmpty(filter.getEmails())) {
            builder.in(user.as(QUsualUser.class).email.toLowerCase(), filter.getEmails().stream().map(String::toLowerCase).toList());
        }
        if (Boolean.TRUE.equals(filter.getIsSystemUser())) {
            builder.isTrue(user.instanceOf(SystemUser.class));
        }
        if (Boolean.FALSE.equals(filter.getIsSystemUser())) {
            builder.isTrue(user.instanceOf(UsualUser.class));
        }
        return builder.build();
    }

    @Override
    public Predicate getAccessControlPredicate() {
        if (!AuthExpressionsHolder.hasPermission(Permission.USER_READ)) {
            return PredicateBuilder.builder()
                    .eq(user.id, AuthenticationHolder.getCurrentUserIdOrElseThrow())
                    .build();
        }
        return null;
    }

    @Override
    protected Sort.Order toOrder(UserSort sortName, SearchRequest.SortDirection direction) {
        return switch (sortName) {
            case CREATED_AT -> buildOrder(user.createdAt, direction);
            case FULL_NAME -> buildOrder(user.fullName, direction);
        };
    }

    @Override
    protected Class<UserFilterDto> getFilterDtoClass() {
        return UserFilterDto.class;
    }
}
