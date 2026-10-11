package com.tms.profilemanager.app.controller;

import com.tms.profilemanager.app.entity.Vendor;
import com.tms.profilemanager.app.mapper.entity.VendorMapper;
import com.tms.profilemanager.app.service.VendorService;
import com.tms.profilemanager.dto.VendorDto;
import com.tms.profilemanager.dto.create.VendorCreateDto;
import com.tms.profilemanager.dto.enums.sort.VendorSort;
import com.tms.profilemanager.dto.filter.VendorFilterDto;
import com.tms.profilemanager.dto.patch.VendorPatchDto;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/vendor")
@Tag(name = "Vendors", description = "Вендоры")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class VendorController extends
        AbstractCrudController<Vendor, VendorDto, VendorCreateDto, VendorPatchDto, VendorFilterDto, VendorSort> {

    public VendorController(VendorService service,
                            VendorMapper mapper) {
        super(service, mapper);
    }
}
