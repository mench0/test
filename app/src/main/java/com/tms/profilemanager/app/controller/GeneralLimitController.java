package com.tms.profilemanager.app.controller;

import com.tms.profilemanager.app.entity.GeneralLimit;
import com.tms.profilemanager.app.mapper.entity.GeneralLimitMapper;
import com.tms.profilemanager.app.service.GeneralLimitService;
import com.tms.profilemanager.dto.GeneralLimitDto;
import com.tms.profilemanager.dto.create.limit.GeneralLimitCreateDto;
import com.tms.profilemanager.dto.enums.sort.GeneralLimitSort;
import com.tms.profilemanager.dto.filter.GeneralLimitFilterDto;
import com.tms.profilemanager.dto.patch.GeneralLimitPatchDto;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/general-limit")
@Tag(name = "Limits", description = "Лимиты")
public class GeneralLimitController extends
        AbstractCrudController<GeneralLimit, GeneralLimitDto, GeneralLimitCreateDto, GeneralLimitPatchDto, GeneralLimitFilterDto, GeneralLimitSort> {
    public GeneralLimitController(GeneralLimitService service,
                                  GeneralLimitMapper mapper) {
        super(service, mapper);
    }
}
