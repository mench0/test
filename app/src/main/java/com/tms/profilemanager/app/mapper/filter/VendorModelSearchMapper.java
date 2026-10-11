package com.tms.profilemanager.app.mapper.filter;

import com.querydsl.core.types.Predicate;
import com.tms.profilemanager.app.mapper.filter.utils.PredicateBuilder;
import com.tms.profilemanager.dto.enums.sort.VendorModelSort;
import com.tms.profilemanager.dto.filter.SearchRequest;
import com.tms.profilemanager.dto.filter.VendorModelFilterDto;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import static com.tms.profilemanager.app.config.security.AuthExpressionsHolder.hasPermission;
import static com.tms.profilemanager.app.entity.QVendor.vendor;
import static com.tms.profilemanager.app.entity.QVendorModel.vendorModel;
import static com.tms.profilemanager.app.mapper.filter.utils.SortBuilder.buildOrder;
import static com.tms.profilemanager.dto.enums.Permission.TERMINAL_READ;

@Component
public class VendorModelSearchMapper extends SearchMapper<VendorModelFilterDto, VendorModelSort> {

    @Override
    protected Predicate toPredicateExceptSubFilters(VendorModelFilterDto filter) {
        return PredicateBuilder.builder()
                .in(vendorModel.id, filter.getIds())
                .in(vendorModel.name, filter.getNames())
                .build();    }

    @Override
    public Predicate getAccessControlPredicate() {
        return hasPermission(TERMINAL_READ) ? null : PredicateBuilder.buildAlwaysFalsePredicate();
    }

    @Override
    protected Sort.Order toOrder(VendorModelSort sortName, SearchRequest.SortDirection direction) {
        return switch (sortName) {
            case CREATED_AT -> buildOrder(vendor.createdAt, direction);
        };    }

    @Override
    protected Class<VendorModelFilterDto> getFilterDtoClass() {
        return VendorModelFilterDto.class;
    }

}
