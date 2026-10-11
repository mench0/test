package com.tms.profilemanager.app.service;

import com.tms.profilemanager.app.entity.dictionary.CardBinRange;
import com.tms.profilemanager.app.entity.event.EntityEvent;
import com.tms.profilemanager.app.mapper.filter.CardBinRangeSearchMapper;
import com.tms.profilemanager.app.repository.CardBinRangeRepository;
import com.tms.profilemanager.app.service.provider.CardBinRangeProvider;
import com.tms.profilemanager.dto.create.dictionary.CardBinRangeCreateDto;
import com.tms.profilemanager.dto.filter.CardBinRangeFilterDto;
import com.tms.profilemanager.dto.patch.dictionary.CardBinRangePatchDto;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;
import org.springframework.validation.annotation.Validated;

import java.util.List;

import static com.tms.profilemanager.app.config.security.AuthExpressionsHolder.hasPermission;
import static com.tms.profilemanager.dto.enums.Permission.TERMINAL_WRITE;
import static java.util.Objects.requireNonNull;

@Service
@Validated
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CardBinRangeService extends AbstractCrudService<CardBinRange, CardBinRangeCreateDto, CardBinRangePatchDto, CardBinRangeFilterDto> {

    private static final String CARD_BIN_RANGE_CACHE = "card-bin-range";

    CardBinRangeRepository cardBinRangeRepository;
    CacheManager cacheManager;

    protected CardBinRangeService(CardBinRangeProvider entityProvider,
                                  CardBinRangeRepository cardBinRangeRepository,
                                  CardBinRangeSearchMapper searchMapper,
                                  ApplicationEventPublisher eventPublisher,
                                  CacheManager cacheManager) {
        super(CardBinRange.class, entityProvider, cardBinRangeRepository, cardBinRangeRepository, searchMapper, eventPublisher);
        this.cardBinRangeRepository = cardBinRangeRepository;
        this.cacheManager = cacheManager;
    }

    @Cacheable(cacheNames = CARD_BIN_RANGE_CACHE, unless = "#result.size() == 0")
    public List<CardBinRange> getCardBinRangesUsingCacheWithoutAccessControl() {
        return cardBinRangeRepository.findAll();
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void evictCache(EntityEvent<?> event) {
        evictCacheIfCardBinRangeChanged(event);
    }

    private void evictCacheIfCardBinRangeChanged(EntityEvent<?> event) {
        if (event.entityClass().equals(CardBinRange.class)) {
            requireNonNull(cacheManager.getCache(CARD_BIN_RANGE_CACHE)).clear();
        }
    }

    @Override
    protected boolean hasWriteAccess() {
        return hasPermission(TERMINAL_WRITE);
    }
}
