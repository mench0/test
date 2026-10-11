package com.tms.profilemanager.app.test.utils.generator;

import com.tms.profilemanager.app.config.security.RunAsSystemUser;
import com.tms.profilemanager.app.controller.VendorController;
import com.tms.profilemanager.dto.VendorDto;
import com.tms.profilemanager.dto.create.VendorCreateDto;
import com.tms.profilemanager.dto.create.VendorCreateDto.VendorModelInputDto;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Component
@Transactional
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class VendorGenerator {

    VendorController vendorController;

    @RunAsSystemUser
    public VendorDto createAndSave(int number) {
        return vendorController.create(buildCreateDto(number));
    }

    public VendorCreateDto buildCreateDto(int number) {
        return VendorCreateDto.builder()
                .name("a" + number)
                .models(Set.of(VendorModelInputDto.builder().name("b" + number).build()))
                .build();
    }
}
