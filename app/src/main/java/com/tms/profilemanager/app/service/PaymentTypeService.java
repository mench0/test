package com.tms.profilemanager.app.service;

import com.tms.profilemanager.app.entity.PaymentType;
import com.tms.profilemanager.app.entity.event.EntityEvent;
import com.tms.profilemanager.app.mapper.filter.PaymentTypeSearchMapper;
import com.tms.profilemanager.app.repository.PaymentTypeRepository;
import com.tms.profilemanager.app.service.provider.PaymentTypeProvider;
import com.tms.profilemanager.dto.create.PaymentTypeCreateDto;
import com.tms.profilemanager.dto.filter.PaymentTypeFilterDto;
import com.tms.profilemanager.dto.patch.PaymentTypePatchDto;
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
import static com.tms.profilemanager.dto.enums.Permission.PAYMENT_TYPE_WRITE;
import static java.util.Objects.requireNonNull;

@Service
@Validated
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PaymentTypeService extends AbstractCrudService<PaymentType, PaymentTypeCreateDto, PaymentTypePatchDto, PaymentTypeFilterDto> {

    private static final String PAYMENT_TYPES_CACHE = "payment-types";

    PaymentTypeRepository paymentTypeRepository;
    CacheManager cacheManager;

    protected PaymentTypeService(PaymentTypeProvider entityProvider,
                                 PaymentTypeSearchMapper searchMapper,
                                 ApplicationEventPublisher eventPublisher,
                                 PaymentTypeRepository paymentTypeRepository,
                                 CacheManager cacheManager) {
        super(PaymentType.class, entityProvider, paymentTypeRepository, paymentTypeRepository, searchMapper, eventPublisher);
        this.paymentTypeRepository = paymentTypeRepository;
        this.cacheManager = cacheManager;
    }

    @Cacheable(cacheNames = PAYMENT_TYPES_CACHE, unless = "#result.size() == 0")
    public List<PaymentType> getAllPaymentTypesUsingCacheWithoutAccessControl() {
        return paymentTypeRepository.findAll();
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void evictCache(EntityEvent<?> event) {
        evictCacheIfPaymentTypeChanged(event);
    }

    private void evictCacheIfPaymentTypeChanged(EntityEvent<?> event) {
        if (event.entityClass().equals(PaymentType.class)) {
            requireNonNull(cacheManager.getCache(PAYMENT_TYPES_CACHE)).clear();
        }
    }
    @Override
    protected boolean hasWriteAccess() {
        return hasPermission(PAYMENT_TYPE_WRITE);
    }
}
