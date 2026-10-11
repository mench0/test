package com.tms.profilemanager.app.controller;

import com.tms.profilemanager.app.entity.Merchant;
import com.tms.profilemanager.app.mapper.entity.MerchantMapper;
import com.tms.profilemanager.app.service.MerchantService;
import com.tms.profilemanager.dto.MerchantDto;
import com.tms.profilemanager.dto.create.MerchantCreateDto;
import com.tms.profilemanager.dto.enums.sort.MerchantSort;
import com.tms.profilemanager.dto.filter.MerchantFilterDto;
import com.tms.profilemanager.dto.patch.MerchantPatchDto;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/merchant")
@Tag(name = "Merchants", description = "Мерчанты")
public class MerchantController extends
        AbstractCrudController<Merchant, MerchantDto, MerchantCreateDto, MerchantPatchDto, MerchantFilterDto, MerchantSort> {

    public MerchantController(MerchantService service, MerchantMapper mapper) {
        super(service, mapper);
    }
}
