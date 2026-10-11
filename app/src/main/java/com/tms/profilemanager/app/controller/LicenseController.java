package com.tms.profilemanager.app.controller;

import com.tms.profilemanager.app.entity.License;
import com.tms.profilemanager.app.mapper.entity.LicenseMapper;
import com.tms.profilemanager.app.service.LicenseService;
import com.tms.profilemanager.app.service.LicenseXlsFileService;
import com.tms.profilemanager.dto.LicenseDto;
import com.tms.profilemanager.dto.create.LicenseCreateDto;
import com.tms.profilemanager.dto.enums.sort.LicenseSort;
import com.tms.profilemanager.dto.filter.LicenseFilterDto;
import com.tms.profilemanager.dto.patch.LicensePatchDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/license")
@Tag(name = "License", description = "Лицензии")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class LicenseController  extends
        AbstractCrudController<License, LicenseDto, LicenseCreateDto, LicensePatchDto, LicenseFilterDto, LicenseSort>{

    LicenseService licenseService;
    LicenseXlsFileService licenseXlsFileService;

    public LicenseController(LicenseService service,
                             LicenseMapper mapper, LicenseService licenseService, LicenseXlsFileService licenseXlsFileService) {
        super(service, mapper);
        this.licenseService = licenseService;
        this.licenseXlsFileService = licenseXlsFileService;
    }

    @GetMapping("/by-sn/{serial-number}")
    @Operation(summary = "Получить лицензию по серийному номеру")
    public LicenseDto getLicenseBySerialNumber(@PathVariable("serial-number") String serialNumber) {
        return licenseService.findBySn(serialNumber)
                .map(mapper::toDto)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    @PostMapping(value = "/upload-license", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    @Operation(summary = "Загрузка лицензии")
    public void uploadLicense(@RequestPart("file") MultipartFile file) {
        licenseXlsFileService.upload(file);
    }

}
