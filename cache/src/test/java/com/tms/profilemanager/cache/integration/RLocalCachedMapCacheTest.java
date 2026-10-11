package com.tms.profilemanager.cache.integration;

import com.tms.profilemanager.cache.TestBean;
import com.tms.profilemanager.cache.TestBean.TestRecord;
import com.tms.profilemanager.cache.config.CacheAutoConfiguration;
import com.tms.profilemanager.cache.test.initializer.RedisContainerInitializer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.redisson.api.LocalCachedMapOptions;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.testcontainers.shaded.org.awaitility.Awaitility;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@EnableAutoConfiguration
@ContextConfiguration(
        classes = {
                CacheAutoConfiguration.class,
                TestBean.class
        },
        initializers = RedisContainerInitializer.class
)
public class RLocalCachedMapCacheTest {

    @Autowired
    TestBean testBean;
    @Autowired
    RedissonClient redissonClient;

    @Test
    void cacheTest() {
        var testRecord1 = new TestRecord(UUID.randomUUID(), BigDecimal.valueOf(1.01), 1);
        var testRecord2 = new TestRecord(UUID.randomUUID(), BigDecimal.valueOf(2.01), 1);
        var testRecord3 = new TestRecord(UUID.randomUUID(), BigDecimal.valueOf(3.01), 1);
        var testRecord4 = new TestRecord(UUID.randomUUID(), BigDecimal.valueOf(4.01), 2);
        var testRecord5 = new TestRecord(UUID.randomUUID(), BigDecimal.valueOf(5.01), 2);
        testBean.setTestRecords(List.of(testRecord1, testRecord2, testRecord3, testRecord4, testRecord5));
        testBean.setNumberOfInvocations(0);

        // check caching list without input parameters
        List<TestRecord> resultList1 = testBean.getTestRecords();
        List<TestRecord> resultList2 = testBean.getTestRecords();
        assertEquals(1, testBean.getNumberOfInvocations());
        assertEquals(resultList1, resultList2);
        testBean.evictCache();
        testBean.getTestRecords();
        assertEquals(2, testBean.getNumberOfInvocations());

        testBean.setNumberOfInvocations(0);

        // check caching list with input parameters
        resultList1 = testBean.getTestRecords(1, BigDecimal.ZERO);
        resultList2 = testBean.getTestRecords(1, BigDecimal.ZERO);
        assertEquals(1, testBean.getNumberOfInvocations());
        assertEquals(resultList1, resultList2);
        testBean.evictCache(1, BigDecimal.ZERO);
        testBean.getTestRecords(1, BigDecimal.ZERO);
        assertEquals(2, testBean.getNumberOfInvocations());

        resultList1 = testBean.getTestRecords(2, BigDecimal.ZERO);
        resultList2 = testBean.getTestRecords(2, BigDecimal.ZERO);
        assertEquals(3, testBean.getNumberOfInvocations());
        assertEquals(resultList1, resultList2);
        testBean.evictCache(2, BigDecimal.ZERO);
        testBean.getTestRecords(1, BigDecimal.ZERO); // get records that are in cache and assert that no invocation
        assertEquals(3, testBean.getNumberOfInvocations());

        testBean.setNumberOfInvocations(0);

        // check caching Optional
        var result1 = testBean.getTestRecord(testRecord1.getId()).orElseThrow();
        var result2 = testBean.getTestRecord(testRecord1.getId()).orElseThrow();
        assertEquals(1, testBean.getNumberOfInvocations());
        assertEquals(result1, result2);
        testBean.evictCache(testRecord1.getId());
        testBean.getTestRecord(testRecord1.getId());
        assertEquals(2, testBean.getNumberOfInvocations());

        // emulate another cache (on another instance) and check that it is updated
        var testRecordByIdMap = redissonClient.getLocalCachedMap(
                "test-app::test-record-by-id-cache",
                LocalCachedMapOptions.<String, Object>defaults()
                        .syncStrategy(LocalCachedMapOptions.SyncStrategy.INVALIDATE)
                        .timeToLive(0)
                        .maxIdle(0)
                        .cacheSize(10));
        Awaitility.await()
                .atMost(Duration.ofSeconds(10))
                .until(() -> testRecordByIdMap.size() == 1);
        assertEquals(testBean.getTestRecord(testRecord1.getId()).orElseThrow(), testRecordByIdMap.get(testRecord1.getId().toString()));

        testBean.setNumberOfInvocations(0);

        // check caching primitive type with not primitive input parameter
        var categoryId1 = testBean.getCategoryId(testRecord1);
        var categoryId2 = testBean.getCategoryId(testRecord1);
        var categoryId3 = testBean.getCategoryId(testRecord5);
        assertEquals(2, testBean.getNumberOfInvocations());
        assertEquals(categoryId1, categoryId2);
        assertNotEquals(categoryId2, categoryId3);
        testBean.evictCache(testRecord1);
        testBean.getCategoryId(testRecord1);
        assertEquals(3, testBean.getNumberOfInvocations());
    }
}
