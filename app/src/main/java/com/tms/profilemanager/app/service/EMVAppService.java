package com.tms.profilemanager.app.service;

import com.tms.profilemanager.app.config.security.AuthExpressionsHolder;
import com.tms.profilemanager.app.entity.EMVApp;
import com.tms.profilemanager.app.entity.event.EntityEvent;
import com.tms.profilemanager.app.mapper.filter.EMVAppSearchMapper;
import com.tms.profilemanager.app.repository.EMVAppRepository;
import com.tms.profilemanager.app.service.provider.EMVAppProvider;
import com.tms.profilemanager.dto.create.EMVAppCreateDto;
import com.tms.profilemanager.dto.filter.EMVAppFilterDto;
import com.tms.profilemanager.dto.patch.EMVAppPatchDto;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import javax.validation.constraints.NotNull;
import java.util.List;

import static com.tms.profilemanager.dto.enums.Permission.Values.PAYMENT_SYSTEM_WRITE;
import static java.util.Objects.requireNonNull;

@Service
public class EMVAppService extends AbstractCrudService<EMVApp, EMVAppCreateDto, EMVAppPatchDto, EMVAppFilterDto> {

    private static final String EMV_APPS_BY_PAYMENT_SYSTEM_ID_CACHE = "emv-apps-by-payment-system-id";

    private final CacheManager cacheManager;
    private final EMVAppRepository emvAppRepository;

    public EMVAppService(EMVAppProvider entityProvider,
                         EMVAppRepository repository,
                         EMVAppSearchMapper searchMapper,
                         CacheManager cacheManager,
                         ApplicationEventPublisher eventPublisher) {
        super(EMVApp.class, entityProvider, repository, repository, searchMapper, eventPublisher);
        this.cacheManager = cacheManager;
        this.emvAppRepository = repository;
    }

    @Cacheable(cacheNames = EMV_APPS_BY_PAYMENT_SYSTEM_ID_CACHE, unless = "#result == null")
    public List<EMVApp> getByPaymentSystemIdUsingCacheWithoutAccessControl(@NotNull Long paymentSystemId) {
        return emvAppRepository.findByPaymentSystemId(paymentSystemId);
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    protected void evictCache(EntityEvent<?> event) {
        if (event.entityClass().equals(EMVApp.class)) {
            requireNonNull(cacheManager.getCache(EMV_APPS_BY_PAYMENT_SYSTEM_ID_CACHE)).clear();
        }
    }

    @Override
    protected boolean hasWriteAccess() {
        return AuthExpressionsHolder.hasPermission(PAYMENT_SYSTEM_WRITE);
    }
}
