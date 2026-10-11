package com.tms.profilemanager.cache;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TestBean {

    List<TestRecord> testRecords = new ArrayList<>();

    @Getter
    int numberOfInvocations = 0;

    @Cacheable(cacheNames = "test-records-by-category-id-cache")
    public List<TestRecord> getTestRecords(int categoryId, BigDecimal amountGreaterThan) {
        numberOfInvocations++;
        return testRecords.stream()
                .filter(it -> it.getCategoryId() == categoryId)
                .filter(it -> it.getAmount().compareTo(amountGreaterThan) > 0)
                .toList();
    }

    @CacheEvict(cacheNames = "test-records-by-category-id-cache")
    public void evictCache(int categoryId, BigDecimal amountGreaterThan) {}

    @Cacheable(cacheNames = "test-records-cache")
    public List<TestRecord> getTestRecords() {
        numberOfInvocations++;
        return testRecords;
    }

    @CacheEvict(cacheNames = "test-records-cache")
    public void evictCache() {}

    @Cacheable(cacheNames = "test-record-by-id-cache")
    public Optional<TestRecord> getTestRecord(UUID id) {
        numberOfInvocations++;
        return testRecords.stream().filter(it -> it.getId().equals(id)).findFirst();
    }

    @CacheEvict(cacheNames = "test-record-by-id-cache")
    public void evictCache(UUID id) {}

    @Cacheable(cacheNames = "category-id-cache")
    public int getCategoryId(TestRecord testRecord) {
        numberOfInvocations++;
        return testRecord.getCategoryId();
    }

    @CacheEvict(cacheNames = "category-id-cache")
    public void evictCache(TestRecord testRecord) {}

    @Data
    @AllArgsConstructor
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class TestRecord implements Serializable {
        UUID id;
        BigDecimal amount;
        int categoryId;
    }
}
