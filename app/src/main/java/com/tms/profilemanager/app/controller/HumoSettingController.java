package com.tms.profilemanager.app.controller;

import com.tms.profilemanager.app.entity.HumoSetting;
import com.tms.profilemanager.app.mapper.entity.HumoSettingMapper;
import com.tms.profilemanager.app.service.HumoSettingService;
import com.tms.profilemanager.dto.HumoSettingDto;
import com.tms.profilemanager.dto.create.HumoSettingCreateDto;
import com.tms.profilemanager.dto.enums.sort.HumoSettingSort;
import com.tms.profilemanager.dto.filter.HumoSettingFilterDto;
import com.tms.profilemanager.dto.patch.HumoSettingPatchDto;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/humo-setting")
@Tag(name = "Humo settings", description = "Настройки Humo")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@ConditionalOnProperty(name = "humo.enable", matchIfMissing = true)
public class HumoSettingController extends
        AbstractCrudController<HumoSetting, HumoSettingDto, HumoSettingCreateDto, HumoSettingPatchDto, HumoSettingFilterDto, HumoSettingSort> {

    public HumoSettingController(HumoSettingService service, HumoSettingMapper mapper) {
        super(service, mapper);
    }
}
