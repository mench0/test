package com.tms.profilemanager.app.service;

import com.tms.profilemanager.app.entity.dictionary.CardCode;
import com.tms.profilemanager.app.entity.dictionary.CardType;
import com.tms.profilemanager.app.entity.event.EntityEvent;
import com.tms.profilemanager.app.mapper.filter.CardCodeSearchMapper;
import com.tms.profilemanager.app.repository.CardCodeRepository;
import com.tms.profilemanager.app.service.provider.CardCodeProvider;
import com.tms.profilemanager.dto.CardCodeDto;
import com.tms.profilemanager.dto.create.dictionary.CardCodeCreateDto;
import com.tms.profilemanager.dto.filter.CardCodeFilterDto;
import com.tms.profilemanager.dto.patch.dictionary.CardCodePatchDto;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;
import org.springframework.validation.annotation.Validated;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static com.tms.profilemanager.app.config.security.AuthExpressionsHolder.hasPermission;
import static com.tms.profilemanager.dto.enums.Permission.TERMINAL_WRITE;
import static java.util.Objects.requireNonNull;

@Service
@Validated
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CardCodeService extends AbstractCrudService<CardCode, CardCodeCreateDto, CardCodePatchDto, CardCodeFilterDto> {

    private static final String CARD_CODE_CACHE = "card-code";

    CardCodeRepository cardCodeRepository;
    CacheManager cacheManager;

    protected CardCodeService(CardCodeProvider entityProvider,
                              CardCodeRepository cardCodeRepository,
                              CardCodeSearchMapper searchMapper,
                              ApplicationEventPublisher eventPublisher,
                              CacheManager cacheManager) {
        super(CardCode.class, entityProvider, cardCodeRepository, cardCodeRepository, searchMapper, eventPublisher);
        this.cardCodeRepository = cardCodeRepository;
        this.cacheManager = cacheManager;
    }

    @Cacheable(cacheNames = CARD_CODE_CACHE, key = "'row'", unless = "#result.size() == 0")
    public List<CardCode> getCardCodesUsingCacheWithoutAccessControl() {
        return cardCodeRepository.findAll();
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void evictCache(EntityEvent<?> event) {
        evictCacheIfCardCodeChanged(event);
        evictCacheIfAnyNestedEntityChanged(event);
    }

    @Cacheable(cacheNames = CARD_CODE_CACHE, key = "'grouped'", unless = "#result.size() == 0")
    public List<CardCodeDto.CardTypeCodesDto> getCardTypeCodes() {
        List<String> cardTypeNames = cardCodeRepository.findDistinctCardTypeNames();
        List<CardCodeRepository.CardCodeSlim> codes = cardCodeRepository.findByCardTypeNamesOrdered(cardTypeNames);

        Map<String, CardCodeDto.CardTypeCodesDto> groupedCardCodes = new LinkedHashMap<>();

        for (CardCodeRepository.CardCodeSlim c : codes) {
            groupedCardCodes.computeIfAbsent(
                    c.getName(),
                    k -> new CardCodeDto.CardTypeCodesDto(c.getId(), c.getName(), new ArrayList<>())).getCodes().add(c.getCode());
        }

        return new ArrayList<>(groupedCardCodes.values());
    }

    private void evictCacheIfCardCodeChanged(EntityEvent<?> event) {
        if (event.entityClass().equals(CardCode.class)) {
            requireNonNull(cacheManager.getCache(CARD_CODE_CACHE)).clear();
        }
    }

    private void evictCacheIfAnyNestedEntityChanged(EntityEvent<?> event) {
        List<Class<?>> nestedClasses = List.of(
                CardType.class
        );
        if (nestedClasses.contains(event.entityClass())) {
            requireNonNull(cacheManager.getCache(CARD_CODE_CACHE)).clear();
        }
    }

    @Override
    protected boolean hasWriteAccess() {
        return hasPermission(TERMINAL_WRITE);
    }
}

