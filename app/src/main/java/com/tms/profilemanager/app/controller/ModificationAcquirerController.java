package com.tms.profilemanager.app.controller;

import com.tms.profilemanager.app.entity.modification.ModificationAcquirer;
import com.tms.profilemanager.app.mapper.entity.modifiication.ModificationAcquirerMapper;
import com.tms.profilemanager.app.service.ModificationAcquirerService;
import com.tms.profilemanager.dto.modification.ModificationAcquirerDto;
import com.tms.profilemanager.dto.create.modification.ModificationAcquirerCreateDto;
import com.tms.profilemanager.dto.enums.sort.ModificationAcquirerSort;
import com.tms.profilemanager.dto.filter.ModificationAcquirerFilterDto;
import com.tms.profilemanager.dto.patch.modification.ModificationAcquirerPatchDto;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/modification-acquirer")
@Tag(name = "Modification acquirer", description = "Модификации экваера")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ModificationAcquirerController extends
        AbstractCrudController<ModificationAcquirer, ModificationAcquirerDto, ModificationAcquirerCreateDto, ModificationAcquirerPatchDto, ModificationAcquirerFilterDto, ModificationAcquirerSort> {

    public ModificationAcquirerController(ModificationAcquirerService service, ModificationAcquirerMapper mapper) {
        super(service, mapper);
    }
}
