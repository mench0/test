package com.tms.profilemanager.app.controller;

import com.tms.profilemanager.app.entity.EMVApp;
import com.tms.profilemanager.app.mapper.entity.EMVAppMapper;
import com.tms.profilemanager.app.service.EMVAppService;
import com.tms.profilemanager.dto.EMVAppDto;
import com.tms.profilemanager.dto.create.EMVAppCreateDto;
import com.tms.profilemanager.dto.enums.sort.EMVAppSort;
import com.tms.profilemanager.dto.filter.EMVAppFilterDto;
import com.tms.profilemanager.dto.patch.EMVAppPatchDto;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/emv-app")
@Tag(name = "EMV Apps", description = "EMV приложения")
public class EMVAppController extends
        AbstractCrudController<EMVApp, EMVAppDto, EMVAppCreateDto, EMVAppPatchDto, EMVAppFilterDto, EMVAppSort> {

    public EMVAppController(EMVAppService service, EMVAppMapper mapper) {
        super(service, mapper);
    }
}
