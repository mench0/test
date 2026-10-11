package com.tms.profilemanager.app.controller;

import com.tms.profilemanager.app.entity.modification.ModificationContactlessApp;
import com.tms.profilemanager.app.mapper.entity.modifiication.ModificationContactlessAppMapper;
import com.tms.profilemanager.app.service.ModificationContactlessAppService;
import com.tms.profilemanager.dto.modification.ModificationContactlessAppDto;
import com.tms.profilemanager.dto.create.modification.ModificationContactlessAppCreateDto;
import com.tms.profilemanager.dto.enums.sort.ModificationContactlessAppSort;
import com.tms.profilemanager.dto.filter.ModificationContactlessAppFilterDto;
import com.tms.profilemanager.dto.patch.modification.ModificationContactlessAppPatchDto;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/modification-contactless-app")
@Tag(name = "Modification contactlessApp", description = "Модификации бесконтактных приложений")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ModificationContactlessAppController extends
        AbstractCrudController<ModificationContactlessApp, ModificationContactlessAppDto, ModificationContactlessAppCreateDto, ModificationContactlessAppPatchDto, ModificationContactlessAppFilterDto, ModificationContactlessAppSort> {

    public ModificationContactlessAppController(ModificationContactlessAppService service, ModificationContactlessAppMapper mapper) {
        super(service, mapper);
    }
}
