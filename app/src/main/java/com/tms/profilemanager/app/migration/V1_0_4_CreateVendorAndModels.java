package com.tms.profilemanager.app.migration;

import com.tms.profilemanager.app.config.security.SecurityManager;
import com.tms.profilemanager.app.service.VendorService;
import com.tms.profilemanager.dto.create.VendorCreateDto;
import com.tms.profilemanager.dto.create.VendorCreateDto.VendorModelInputDto;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

/**
 * Migration creates vendor and models
 */
@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class V1_0_4_CreateVendorAndModels implements Migration {

    SecurityManager securityManager;
    VendorService vendorService;

    @Override
    @Transactional
    public void execute() {
        securityManager.runAsSystemUser(() -> {
            vendorService.create(VendorCreateDto.builder()
                    .name("PAX")
                    .models(Set.of(VendorModelInputDto.builder().name("PAX_A930").build(), VendorModelInputDto.builder().name("PAX_IM30").build(),
                            VendorModelInputDto.builder().name("PAX_A35").build())).build());
        });
    }
}
