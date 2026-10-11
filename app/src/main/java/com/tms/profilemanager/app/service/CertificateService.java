package com.tms.profilemanager.app.service;

import com.tms.profilemanager.app.config.security.AuthExpressionsHolder;
import com.tms.profilemanager.app.entity.Certificate;
import com.tms.profilemanager.app.entity.event.EntityEvent;
import com.tms.profilemanager.app.mapper.filter.CertificateSearchMapper;
import com.tms.profilemanager.app.repository.CertificateRepository;
import com.tms.profilemanager.app.service.provider.CertificateProvider;
import com.tms.profilemanager.dto.create.CertificateCreateDto;
import com.tms.profilemanager.dto.filter.CertificateFilterDto;
import com.tms.profilemanager.dto.patch.CertificatePatchDto;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotNull;
import java.util.List;

import static com.tms.profilemanager.dto.enums.Permission.Values.PAYMENT_SYSTEM_WRITE;
import static java.util.Objects.requireNonNull;

@Service
@Validated
public class CertificateService extends
        AbstractCrudService<Certificate, CertificateCreateDto, CertificatePatchDto, CertificateFilterDto> {

    private static final String CERTIFICATES_BY_PAYMENT_SYSTEM_ID_CACHE = "certificates-by-payment-system-id";

    private final CacheManager cacheManager;
    private final CertificateRepository certificateRepository;

    public CertificateService(CertificateProvider entityProvider,
                              CertificateRepository repository,
                              CertificateSearchMapper searchMapper,
                              CacheManager cacheManager,
                              ApplicationEventPublisher eventPublisher) {
        super(Certificate.class, entityProvider, repository, repository, searchMapper, eventPublisher);
        this.cacheManager = cacheManager;
        this.certificateRepository = repository;
    }

    @Cacheable(cacheNames = CERTIFICATES_BY_PAYMENT_SYSTEM_ID_CACHE, unless = "#result == null")
    public List<Certificate> getByPaymentSystemIdUsingCacheWithoutAccessControl(@NotNull Long paymentSystemId) {
        return certificateRepository.findByPaymentSystemId(paymentSystemId);
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    protected void evictCache(EntityEvent<?> event) {
        if (event.entityClass().equals(Certificate.class)) {
            requireNonNull(cacheManager.getCache(CERTIFICATES_BY_PAYMENT_SYSTEM_ID_CACHE)).clear();
        }
    }

    @Override
    protected boolean hasWriteAccess() {
        return AuthExpressionsHolder.hasPermission(PAYMENT_SYSTEM_WRITE);
    }
}
