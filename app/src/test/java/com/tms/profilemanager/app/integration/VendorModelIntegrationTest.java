package com.tms.profilemanager.app.integration;

import com.tms.profilemanager.app.entity.VendorModel;
import com.tms.profilemanager.app.service.VendorModelService;
import com.tms.profilemanager.app.service.VendorService;
import com.tms.profilemanager.dto.VendorDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.clearInvocations;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class VendorModelIntegrationTest extends AbstractIntegrationTest {
    
    @Autowired
    VendorModelService vendorModelService;
    @Autowired
    VendorService vendorService;

    @BeforeEach
    public void beforeEach() {
        databaseCleaner.clearDatabaseAndExecuteJavaMigrations();
        requestSender.setCurrentUser(ADMIN);
    }

    @Test
    void cacheByIdTest() {
        clearInvocations(vendorModelRepository);

        // get from database because cache is empty
        assertTrue(vendorModelService.getByIdUsingCacheWithoutAccessControl(1_000_000L).isEmpty());
        verify(vendorModelRepository, times(1)).findById(any(Long.class));

        // get from database because result == null (null values are not cached)
        assertTrue(vendorModelService.getByIdUsingCacheWithoutAccessControl(1_000_000L).isEmpty());
        verify(vendorModelRepository, times(2)).findById(any(Long.class));

        // create new vendor model
        VendorDto createdVendor = vendorGenerator.createAndSave(1);
        Long vendorModelId = createdVendor.getModels().iterator().next().getId();
        VendorModel createdVendorModel = vendorModelRepository.findById(vendorModelId).orElseThrow();

        clearInvocations(vendorModelRepository);

        // get from database because cache is empty
        assertEquals(createdVendorModel, vendorModelService.getByIdUsingCacheWithoutAccessControl(vendorModelId).orElseThrow());
        verify(vendorModelRepository, times(1)).findById(any(Long.class));

        // get from cache and get the same result
        assertEquals(createdVendorModel, vendorModelService.getByIdUsingCacheWithoutAccessControl(vendorModelId).orElseThrow());
        verify(vendorModelRepository, times(1)).findById(any(Long.class));

        // delete vendor and its models
        vendorTestClient.deleteById(createdVendor.getId());

        clearInvocations(vendorModelRepository);

        // get from database because cache is empty
        assertTrue(vendorModelService.getByIdUsingCacheWithoutAccessControl(vendorModelId).isEmpty());
        verify(vendorModelRepository, times(1)).findById(any(Long.class));
    }
}
