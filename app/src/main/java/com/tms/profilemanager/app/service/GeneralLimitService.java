package com.tms.profilemanager.app.service;

import com.tms.profilemanager.app.config.security.AuthExpressionsHolder;
import com.tms.profilemanager.app.entity.ContactLimit;
import com.tms.profilemanager.app.entity.ContactlessLimit;
import com.tms.profilemanager.app.entity.DRL;
import com.tms.profilemanager.app.entity.GeneralLimit;
import com.tms.profilemanager.app.entity.event.EntityEvent;
import com.tms.profilemanager.app.mapper.filter.GeneralLimitSearchMapper;
import com.tms.profilemanager.app.repository.GeneralLimitRepository;
import com.tms.profilemanager.app.service.provider.limit.GeneralLimitProvider;
import com.tms.profilemanager.dto.create.limit.GeneralLimitCreateDto;
import com.tms.profilemanager.dto.filter.GeneralLimitFilterDto;
import com.tms.profilemanager.dto.patch.GeneralLimitPatchDto;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.tms.profilemanager.dto.enums.Permission.PAYMENT_SYSTEM_WRITE;
import static java.util.Objects.requireNonNull;

@Service
public class GeneralLimitService extends
        AbstractCrudService<GeneralLimit, GeneralLimitCreateDto, GeneralLimitPatchDto, GeneralLimitFilterDto> {

    private static final String LIMITS_CACHE = "limits-by-ids";

    private final CacheManager cacheManager;

    protected GeneralLimitService(GeneralLimitProvider entityProvider,
                                  GeneralLimitRepository generalLimitRepository,
                                  GeneralLimitSearchMapper searchMapper,
                                  ApplicationEventPublisher eventPublisher,
                                  CacheManager cacheManager) {
        super(GeneralLimit.class, entityProvider, generalLimitRepository, generalLimitRepository, searchMapper, eventPublisher);
        this.cacheManager = cacheManager;
    }

    @Cacheable(value = LIMITS_CACHE, unless = "#result.size() == 0")
    public Set<GeneralLimit> getAllByIdsLimitsUsingCacheWithoutAccessControl(Set<Long> ids) {
        return  new HashSet<>(repository.findAllById(ids));
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    protected void evictCache(EntityEvent<?> event) {
        evictCacheIfGeneralLimitChanged(event);
        evictCacheIfAnyNestedEntityChanged(event);
    }

    @Override
    protected boolean hasWriteAccess() {
        return AuthExpressionsHolder.hasPermission(PAYMENT_SYSTEM_WRITE);
    }

    private void evictCacheIfGeneralLimitChanged(EntityEvent<?> event) {
        if (event.entityClass().equals(GeneralLimit.class)) {
            requireNonNull(cacheManager.getCache(LIMITS_CACHE)).clear();
        }
    }

    private void evictCacheIfAnyNestedEntityChanged(EntityEvent<?> event) {
        List<Class<?>> nestedClasses = List.of(
                ContactLimit.class,
                ContactlessLimit.class,
                DRL.class
        );
        if (nestedClasses.contains(event.entityClass())) {
            requireNonNull(cacheManager.getCache(LIMITS_CACHE)).clear();
        }
    }
}
