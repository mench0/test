package com.tms.profilemanager.app.service;

import com.tms.profilemanager.app.config.security.AuthExpressionsHolder;
import com.tms.profilemanager.app.entity.ContactlessApp;
import com.tms.profilemanager.app.entity.event.EntityEvent;
import com.tms.profilemanager.app.mapper.filter.ContactlessAppSearchMapper;
import com.tms.profilemanager.app.repository.ContactlessAppRepository;
import com.tms.profilemanager.app.service.provider.ContactlessAppProvider;
import com.tms.profilemanager.dto.create.ContactlessAppCreateDto;
import com.tms.profilemanager.dto.filter.ContactlessAppFilterDto;
import com.tms.profilemanager.dto.patch.ContactlessAppPatchDto;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import javax.validation.constraints.NotNull;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.tms.profilemanager.dto.enums.Permission.Values.PAYMENT_SYSTEM_WRITE;
import static java.util.Objects.requireNonNull;

@Service
public class ContactlessAppService extends
        AbstractCrudService<ContactlessApp, ContactlessAppCreateDto, ContactlessAppPatchDto, ContactlessAppFilterDto> {

    private static final String CONTACTLESS_APPS_BY_PAYMENT_SYSTEM_ID_CACHE = "contactless-apps-by-payment-system-id";

    private final CacheManager cacheManager;
    private final ContactlessAppRepository contactlessAppRepository;

    public ContactlessAppService(ContactlessAppProvider entityProvider,
                                 ContactlessAppRepository repository,
                                 ContactlessAppSearchMapper searchMapper,
                                 CacheManager cacheManager,
                                 ApplicationEventPublisher eventPublisher) {
        super(ContactlessApp.class, entityProvider, repository, repository, searchMapper, eventPublisher);
        this.cacheManager = cacheManager;
        this.contactlessAppRepository = repository;
    }

    @Cacheable(cacheNames = CONTACTLESS_APPS_BY_PAYMENT_SYSTEM_ID_CACHE, unless = "#result == null")
    public List<ContactlessApp> getByPaymentSystemIdUsingCacheWithoutAccessControl(@NotNull Long paymentSystemId) {
        return contactlessAppRepository.findByPaymentSystemId(paymentSystemId);
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    protected void evictCache(EntityEvent<?> event) {
        if (event.entityClass().equals(ContactlessApp.class)) {
            requireNonNull(cacheManager.getCache(CONTACTLESS_APPS_BY_PAYMENT_SYSTEM_ID_CACHE)).clear();
        }
    }

    @Transactional(readOnly = true)
    public Set<ContactlessApp> getByPaymentSystemIdAndNotArchived(@NotNull Long paymentSystemId) {
        return new HashSet<>(search(ContactlessAppFilterDto.builder()
                .paymentSystemId(paymentSystemId)
                .archived(false)
                .build()));
    }

    @Override
    protected boolean hasWriteAccess() {
        return AuthExpressionsHolder.hasPermission(PAYMENT_SYSTEM_WRITE);
    }
}
