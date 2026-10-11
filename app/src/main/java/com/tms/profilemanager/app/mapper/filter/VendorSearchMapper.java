package com.tms.profilemanager.app.mapper.filter;

import com.querydsl.core.types.Predicate;
import com.tms.profilemanager.app.mapper.filter.utils.PredicateBuilder;
import com.tms.profilemanager.dto.enums.sort.VendorSort;
import com.tms.profilemanager.dto.filter.SearchRequest;
import com.tms.profilemanager.dto.filter.VendorFilterDto;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import static com.tms.profilemanager.app.config.security.AuthExpressionsHolder.hasPermission;
import static com.tms.profilemanager.app.entity.QVendor.vendor;
import static com.tms.profilemanager.app.mapper.filter.utils.SortBuilder.buildOrder;
import static com.tms.profilemanager.dto.enums.Permission.TERMINAL_READ;

@Component
public class VendorSearchMapper extends SearchMapper<VendorFilterDto, VendorSort> {

    @Override
    protected Predicate toPredicateExceptSubFilters(VendorFilterDto filter) {
        return PredicateBuilder.builder()
                .in(vendor.id, filter.getIds())
                .in(vendor.name, filter.getNames())
                .build();
    }

    @Override
    public Predicate getAccessControlPredicate() {
        return hasPermission(TERMINAL_READ) ? null : PredicateBuilder.buildAlwaysFalsePredicate();
    }

    @Override
    protected Sort.Order toOrder(VendorSort sortName, SearchRequest.SortDirection direction) {
        return switch (sortName) {
            case CREATED_AT -> buildOrder(vendor.createdAt, direction);
        };
    }

    @Override
    protected Class<VendorFilterDto> getFilterDtoClass() {
        return VendorFilterDto.class;
    }

}
