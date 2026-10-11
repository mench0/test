package com.tms.profilemanager.app.service;

import com.tms.profilemanager.app.config.security.AuthExpressionsHolder;
import com.tms.profilemanager.app.entity.Acquirer;
import com.tms.profilemanager.app.entity.AcquirerOperation;
import com.tms.profilemanager.app.entity.event.EntityEvent;
import com.tms.profilemanager.app.mapper.filter.AcquirerSearchMapper;
import com.tms.profilemanager.app.repository.AcquirerRepository;
import com.tms.profilemanager.app.service.provider.AcquirerProvider;
import com.tms.profilemanager.dto.create.AcquirerCreateDto;
import com.tms.profilemanager.dto.enums.Permission;
import com.tms.profilemanager.dto.filter.AcquirerFilterDto;
import com.tms.profilemanager.dto.patch.AcquirerPatchDto;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.toMap;

@Service
public class AcquirerService extends AbstractCrudService<Acquirer, AcquirerCreateDto, AcquirerPatchDto, AcquirerFilterDto> {

    private static final String ACQUIRER_BY_ID_CACHE = "acquirer-by-id";

    private final CacheManager cacheManager;
    private final AcquirerRepository acquirerRepository;

    public AcquirerService(AcquirerProvider entityProvider,
                           AcquirerRepository repository,
                           AcquirerSearchMapper searchMapper,
                           CacheManager cacheManager,
                           ApplicationEventPublisher eventPublisher) {
        super(Acquirer.class, entityProvider, repository, repository, searchMapper, eventPublisher);
        this.cacheManager = cacheManager;
        this.acquirerRepository = repository;
    }

    @Transactional(readOnly = true)
    public Map<Long, String> getAcquirersName(Set<Long> acquirerIds) {
        return acquirerRepository.getAcquirersName(acquirerIds).stream()
                .collect(toMap(AcquirerRepository.AcquirerName::getId, AcquirerRepository.AcquirerName::getName));
    }

    @Cacheable(cacheNames = ACQUIRER_BY_ID_CACHE, unless = "#result == null")
    public Optional<Acquirer> getByIdUsingCacheWithoutAccessControl(@NotNull Long id) {
        return acquirerRepository.getByIdAndArchivedFalse(id);
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    protected void evictCache(EntityEvent<?> event) {
        evictCacheIfAcquirerChanged(event);
        evictCacheIfAnyNestedEntityChanged(event);
    }

    @Override
    protected boolean hasWriteAccess() {
        return AuthExpressionsHolder.hasPermission(Permission.ACQUIRER_WRITE);
    }

    private void evictCacheIfAcquirerChanged(EntityEvent<?> event) {
        if (event.entityClass().equals(Acquirer.class)) {
            requireNonNull(cacheManager.getCache(ACQUIRER_BY_ID_CACHE)).evict(event.entityId());
        }
    }

    private void evictCacheIfAnyNestedEntityChanged(EntityEvent<?> event) {
        List<Class<?>> nestedClasses = List.of(
                AcquirerOperation.class
        );
        if (nestedClasses.contains(event.entityClass())) {
            requireNonNull(cacheManager.getCache(ACQUIRER_BY_ID_CACHE)).clear();
        }
    }
}
