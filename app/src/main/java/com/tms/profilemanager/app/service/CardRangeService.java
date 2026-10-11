package com.tms.profilemanager.app.service;

import com.tms.profilemanager.app.config.security.AuthExpressionsHolder;
import com.tms.profilemanager.app.entity.CardRange;
import com.tms.profilemanager.app.entity.ContactLimit;
import com.tms.profilemanager.app.entity.ContactlessLimit;
import com.tms.profilemanager.app.entity.DRL;
import com.tms.profilemanager.app.entity.GeneralLimit;
import com.tms.profilemanager.app.entity.event.EntityEvent;
import com.tms.profilemanager.app.mapper.filter.CardRangeSearchMapper;
import com.tms.profilemanager.app.repository.CardRangeRepository;
import com.tms.profilemanager.app.service.provider.CardRangeProvider;
import com.tms.profilemanager.dto.create.CardRangeCreateDto;
import com.tms.profilemanager.dto.filter.CardRangeFilterDto;
import com.tms.profilemanager.dto.patch.CardRangePatchDto;
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
public class CardRangeService extends
        AbstractCrudService<CardRange, CardRangeCreateDto, CardRangePatchDto, CardRangeFilterDto> {

    private static final String CARD_RANGES_BY_ACQUIRER_ID_CACHE = "card-ranges-by-acquirer-id";

    private final CacheManager cacheManager;
    private final CardRangeRepository cardRangeRepository;

    public CardRangeService(CardRangeProvider entityProvider,
                            CardRangeRepository repository,
                            CardRangeSearchMapper searchMapper,
                            CacheManager cacheManager,
                            ApplicationEventPublisher eventPublisher) {
        super(CardRange.class, entityProvider, repository, repository, searchMapper, eventPublisher);
        this.cacheManager = cacheManager;
        this.cardRangeRepository = repository;
    }

    @Cacheable(cacheNames = CARD_RANGES_BY_ACQUIRER_ID_CACHE, unless = "#result.size() == 0")
    public List<CardRange> getByAcquirerIdUsingCacheWithoutAccessControl(@NotNull Long acquirerId) {
        return cardRangeRepository.getByAcquirerId(acquirerId);
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    protected void evictCache(EntityEvent<?> event) {
        evictCacheIfCardRangeChanged(event);
        evictCacheIfAnyNestedEntityChanged(event);
    }

    @Override
    protected boolean hasWriteAccess() {
        return AuthExpressionsHolder.hasPermission(PAYMENT_SYSTEM_WRITE);
    }

    private void evictCacheIfCardRangeChanged(EntityEvent<?> event) {
        if (event.entityClass().equals(CardRange.class)) {
            requireNonNull(cacheManager.getCache(CARD_RANGES_BY_ACQUIRER_ID_CACHE)).clear();
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
            requireNonNull(cacheManager.getCache(CARD_RANGES_BY_ACQUIRER_ID_CACHE)).clear();
        }
    }
}
