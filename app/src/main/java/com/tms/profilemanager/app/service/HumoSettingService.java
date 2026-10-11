package com.tms.profilemanager.app.service;

import com.tms.profilemanager.app.config.security.AuthExpressionsHolder;
import com.tms.profilemanager.app.entity.HumoSetting;
import com.tms.profilemanager.app.entity.event.EntityEvent;
import com.tms.profilemanager.app.mapper.filter.HumoSettingSearchMapper;
import com.tms.profilemanager.app.repository.HumoSettingRepository;
import com.tms.profilemanager.app.service.provider.HumoSettingProvider;
import com.tms.profilemanager.dto.create.HumoSettingCreateDto;
import com.tms.profilemanager.dto.filter.HumoSettingFilterDto;
import com.tms.profilemanager.dto.patch.HumoSettingPatchDto;
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

import static com.tms.profilemanager.dto.enums.Permission.Values.TERMINAL_WRITE;
import static java.util.Objects.requireNonNull;

@Service
@Validated
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class HumoSettingService extends AbstractCrudService<HumoSetting, HumoSettingCreateDto, HumoSettingPatchDto, HumoSettingFilterDto> {

    private static final String HUMO_SETTINGS_CACHE = "humo-settings";

    private final CacheManager cacheManager;

    protected HumoSettingService(HumoSettingProvider entityProvider,
                                 HumoSettingRepository repository,
                                 HumoSettingSearchMapper searchMapper,
                                 ApplicationEventPublisher eventPublisher,
                                 CacheManager cacheManager) {
        super(HumoSetting.class, entityProvider, repository, repository, searchMapper, eventPublisher);
        this.cacheManager = cacheManager;
    }

    @Cacheable(cacheNames = HUMO_SETTINGS_CACHE, unless = "#result.size() == 0")
    public List<HumoSetting> getAllHumoSettingsUsingCacheWithoutAccessControl() {
        return repository.findAll();
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    protected void evictCache(EntityEvent<?> event) {
        evictCacheIfHumoSettingChanged(event);
    }

    private void evictCacheIfHumoSettingChanged(EntityEvent<?> event) {
        if (event.entityClass().equals(HumoSetting.class)) {
            requireNonNull(cacheManager.getCache(HUMO_SETTINGS_CACHE)).clear();
        }
    }

    @Override
    protected boolean hasWriteAccess() {
        return AuthExpressionsHolder.hasPermission(TERMINAL_WRITE);
    }
}
