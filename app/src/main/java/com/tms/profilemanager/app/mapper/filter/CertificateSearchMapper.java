package com.tms.profilemanager.app.mapper.filter;

import com.querydsl.core.types.Predicate;
import com.tms.profilemanager.app.config.security.AuthExpressionsHolder;
import com.tms.profilemanager.app.mapper.filter.utils.PredicateBuilder;
import com.tms.profilemanager.dto.enums.sort.CertificateSort;
import com.tms.profilemanager.dto.filter.CertificateFilterDto;
import com.tms.profilemanager.dto.filter.SearchRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import static com.tms.profilemanager.app.entity.QCertificate.certificate;
import static com.tms.profilemanager.app.mapper.filter.utils.SortBuilder.buildOrder;
import static com.tms.profilemanager.dto.enums.Permission.PAYMENT_SYSTEM_READ;

@Component
public class CertificateSearchMapper extends SearchMapper<CertificateFilterDto, CertificateSort> {

    @Override
    protected Predicate toPredicateExceptSubFilters(CertificateFilterDto filter) {
        return PredicateBuilder.builder()
                .in(certificate.id, filter.getIds())
                .eq(certificate.archived, filter.getArchived())
                .in(certificate.paymentSystemId, filter.getPaymentSystemIds())
                .build();
    }

    @Override
    public Predicate getAccessControlPredicate() {
        return AuthExpressionsHolder.hasPermission(PAYMENT_SYSTEM_READ) ? null : PredicateBuilder.buildAlwaysFalsePredicate();
    }

    @Override
    protected Sort.Order toOrder(CertificateSort sortName, SearchRequest.SortDirection direction) {
        return switch (sortName) {
            case CREATED_AT -> buildOrder(certificate.createdAt, direction);
        };
    }

    @Override
    protected Class<CertificateFilterDto> getFilterDtoClass() {
        return CertificateFilterDto.class;
    }
}
