package com.tms.profilemanager.app.service;

import com.tms.profilemanager.app.entity.dictionary.CardType;
import com.tms.profilemanager.app.entity.event.EntityEvent;
import com.tms.profilemanager.app.mapper.filter.CardTypeSearchMapper;
import com.tms.profilemanager.app.repository.CardTypeRepository;
import com.tms.profilemanager.app.service.provider.CardTypeProvider;
import com.tms.profilemanager.dto.create.dictionary.CardTypeCreateDto;
import com.tms.profilemanager.dto.filter.CardTypeFilterDto;
import com.tms.profilemanager.dto.patch.dictionary.CardTypePatchDto;
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
public class CardTypeService extends AbstractCrudService<CardType, CardTypeCreateDto, CardTypePatchDto, CardTypeFilterDto> {

    private static final String CARD_TYPE_CACHE = "card-type";

    CardTypeRepository cardTypeRepository;
    CacheManager cacheManager;

    protected CardTypeService(CardTypeProvider entityProvider,
                              CardTypeRepository cardTypeRepository,
                              CardTypeSearchMapper searchMapper,
                              ApplicationEventPublisher eventPublisher,
                              CacheManager cacheManager) {
        super(CardType.class, entityProvider, cardTypeRepository, cardTypeRepository, searchMapper, eventPublisher);
        this.cardTypeRepository = cardTypeRepository;
        this.cacheManager = cacheManager;
    }

    @Cacheable(cacheNames = CARD_TYPE_CACHE, unless = "#result.size() == 0")
    public List<CardType> getCardTypeUsingCacheWithoutAccessControl() {
        return cardTypeRepository.findAll();
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void evictCache(EntityEvent<?> event) {
        evictCacheIfCardTypeChanged(event);
    }

    private void evictCacheIfCardTypeChanged(EntityEvent<?> event) {
        if (event.entityClass().equals(CardType.class)) {
            requireNonNull(cacheManager.getCache(CARD_TYPE_CACHE)).clear();
        }
    }

    @Override
    protected boolean hasWriteAccess() {
        return hasPermission(TERMINAL_WRITE);
    }
}
