package com.tms.profilemanager.app.controller;

import com.tms.profilemanager.app.entity.modification.ModificationEmvApp;
import com.tms.profilemanager.app.mapper.entity.modifiication.ModificationEmvAppMapper;
import com.tms.profilemanager.app.service.ModificationEmvAppService;
import com.tms.profilemanager.dto.modification.ModificationEmvAppDto;
import com.tms.profilemanager.dto.create.modification.ModificationEmvAppCreateDto;
import com.tms.profilemanager.dto.enums.sort.ModificationEmvAppSort;
import com.tms.profilemanager.dto.filter.ModificationEmvAppFilterDto;
import com.tms.profilemanager.dto.patch.modification.ModificationEmvAppPatchDto;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/modification-emv-app")
@Tag(name = "Modification emvApp", description = "Модификации EMV приложения")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ModificationEmvAppController extends
        AbstractCrudController<ModificationEmvApp, ModificationEmvAppDto, ModificationEmvAppCreateDto, ModificationEmvAppPatchDto, ModificationEmvAppFilterDto, ModificationEmvAppSort> {

    public ModificationEmvAppController(ModificationEmvAppService service, ModificationEmvAppMapper mapper) {
        super(service, mapper);
    }
}
