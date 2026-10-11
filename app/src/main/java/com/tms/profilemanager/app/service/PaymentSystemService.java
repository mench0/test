package com.tms.profilemanager.app.service;

import com.tms.profilemanager.app.entity.ContactLimit;
import com.tms.profilemanager.app.entity.ContactlessLimit;
import com.tms.profilemanager.app.entity.DRL;
import com.tms.profilemanager.app.entity.GeneralLimit;
import com.tms.profilemanager.app.entity.PaymentSystem;
import com.tms.profilemanager.app.entity.event.EntityEvent;
import com.tms.profilemanager.app.mapper.filter.PaymentSystemSearchMapper;
import com.tms.profilemanager.app.repository.PaymentSystemRepository;
import com.tms.profilemanager.app.service.provider.PaymentSystemProvider;
import com.tms.profilemanager.dto.create.PaymentSystemCreateDto;
import com.tms.profilemanager.dto.filter.PaymentSystemFilterDto;
import com.tms.profilemanager.dto.patch.PaymentSystemPatchDto;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Optional;

import static com.tms.profilemanager.app.config.security.AuthExpressionsHolder.hasPermission;
import static com.tms.profilemanager.dto.enums.Permission.Values.PAYMENT_SYSTEM_WRITE;
import static java.util.Objects.requireNonNull;

@Service
@Validated
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PaymentSystemService
        extends AbstractCrudService<PaymentSystem, PaymentSystemCreateDto, PaymentSystemPatchDto, PaymentSystemFilterDto> {

    private static final String PAYMENT_SYSTEM_BY_ID_CACHE = "payment-system-by-id";

    CacheManager cacheManager;

    public PaymentSystemService(PaymentSystemProvider entityProvider,
                                PaymentSystemRepository repository,
                                PaymentSystemSearchMapper searchMapper,
                                CacheManager cacheManager,
                                ApplicationEventPublisher eventPublisher) {
        super(PaymentSystem.class, entityProvider, repository, repository, searchMapper, eventPublisher);
        this.cacheManager = cacheManager;
    }

    @Cacheable(cacheNames = PAYMENT_SYSTEM_BY_ID_CACHE, unless = "#result == null")
    public Optional<PaymentSystem> getByIdUsingCacheWithoutAccessControl(@NotNull Long id) {
        return repository.findById(id);
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    protected void evictCache(EntityEvent<?> event) {
        evictCacheIfPaymentSystemsChanged(event);
        evictCacheIfAnyNestedEntityChanged(event);
    }

    @Override
    protected boolean hasWriteAccess() {
        return hasPermission(PAYMENT_SYSTEM_WRITE);
    }

    private void evictCacheIfPaymentSystemsChanged(EntityEvent<?> event) {
        if (event.entityClass().equals(PaymentSystem.class)) {
            requireNonNull(cacheManager.getCache(PAYMENT_SYSTEM_BY_ID_CACHE)).evict(event.entityId());
        }
    }

    private void evictCacheIfAnyNestedEntityChanged(EntityEvent<?> event) {
        List<Class<?>> nestedClasses = List.of(
                GeneralLimit.class,
                ContactLimit.class,
                ContactlessLimit.class,
                DRL.class
        );
        if (nestedClasses.contains(event.entityClass())) {
            requireNonNull(cacheManager.getCache(PAYMENT_SYSTEM_BY_ID_CACHE)).clear();
        }
    }
}
