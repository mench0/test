package com.tms.profilemanager.app.mapper.filter;

import com.querydsl.core.types.Predicate;
import com.tms.profilemanager.app.mapper.filter.utils.PredicateBuilder;
import com.tms.profilemanager.dto.enums.sort.LicenseSort;
import com.tms.profilemanager.dto.filter.LicenseFilterDto;
import com.tms.profilemanager.dto.filter.SearchRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import static com.tms.profilemanager.app.config.security.AuthExpressionsHolder.hasPermission;
import static com.tms.profilemanager.app.entity.QLicense.license;
import static com.tms.profilemanager.app.mapper.filter.utils.SortBuilder.buildOrder;
import static com.tms.profilemanager.dto.enums.Permission.TERMINAL_READ;

@Component
public class LicenseSearchMapper extends SearchMapper<LicenseFilterDto, LicenseSort> {

    @Override
    protected Predicate toPredicateExceptSubFilters(LicenseFilterDto filter) {
        return PredicateBuilder.builder()
                .in(license.id, filter.getIds())
                .in(license.serialNumber, filter.getSerialNumber())
                .in(license.licenseCode, filter.getLicenseCode())
                .build();
    }

    @Override
    public Predicate getAccessControlPredicate() {
        return hasPermission(TERMINAL_READ) ? null : PredicateBuilder.buildAlwaysFalsePredicate();
    }

    @Override
    protected Sort.Order toOrder(LicenseSort sortName, SearchRequest.SortDirection direction) {
        return switch (sortName) {
            case CREATED_AT -> buildOrder(license.createdAt, direction);
        };
    }

    @Override
    protected Class<LicenseFilterDto> getFilterDtoClass() {
        return LicenseFilterDto.class;
    }
}
