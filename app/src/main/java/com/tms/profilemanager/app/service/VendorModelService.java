package com.tms.profilemanager.app.service;

import com.querydsl.core.types.Predicate;
import com.tms.profilemanager.app.entity.Vendor;
import com.tms.profilemanager.app.entity.VendorModel;
import com.tms.profilemanager.app.entity.event.EntityEvent;
import com.tms.profilemanager.app.exception.EntityNotFoundByFieldException;
import com.tms.profilemanager.app.mapper.filter.VendorModelSearchMapper;
import com.tms.profilemanager.app.repository.VendorModelRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.toMap;

@Service
@Validated
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class VendorModelService {

    private static final String VENDOR_MODEL_BY_ID_CACHE = "vendor-model-by-id";

    CacheManager cacheManager;
    VendorModelRepository vendorModelRepository;
    VendorModelSearchMapper vendorModelSearchMapper;

    @Cacheable(cacheNames = VENDOR_MODEL_BY_ID_CACHE, unless = "#result == null")
    public Optional<VendorModel> getByIdUsingCacheWithoutAccessControl(@NotNull Long id) {
        return vendorModelRepository.findById(id);
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    protected void evictCache(EntityEvent<?> event) {
        evictCacheIfVendorOrVendorModelChanged(event);
    }

    @Transactional(readOnly = true)
    public void existsByIdOrElseThrow(@NotNull Long id) {
        Predicate predicate = vendorModelSearchMapper.toPredicate(Set.of(id));
        if (!vendorModelRepository.exists(predicate)) {
            throw new EntityNotFoundByFieldException(VendorModel.class, "id", id);
        }
    }

    @Transactional(readOnly = true)
    public List<VendorModel> findVendorModelsById(Set<Long> ids) {
        return vendorModelRepository.findAllById(ids);
    }

    @Transactional(readOnly = true)
    public Map<Long, String> getModelsName(Set<Long> modelIds) {
        return vendorModelRepository.getModelsName(modelIds).stream()
                .collect(toMap(VendorModelRepository.ModelName::getId, VendorModelRepository.ModelName::getName));
    }

    private void evictCacheIfVendorOrVendorModelChanged(EntityEvent<?> event) {
        if (event.entityClass().equals(VendorModel.class) || event.entityClass().equals(Vendor.class)) {
            requireNonNull(cacheManager.getCache(VENDOR_MODEL_BY_ID_CACHE)).clear();
        }
    }
}
