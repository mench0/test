package com.tms.profilemanager.app.controller;

import com.tms.profilemanager.app.entity.modification.ModificationTerminal;
import com.tms.profilemanager.app.mapper.entity.modifiication.ModificationTerminalMapper;
import com.tms.profilemanager.app.service.ModificationTerminalService;
import com.tms.profilemanager.dto.create.modification.ModificationTerminalCreateDto;
import com.tms.profilemanager.dto.enums.sort.ModificationTerminalSort;
import com.tms.profilemanager.dto.filter.ModificationTerminalFilterDto;
import com.tms.profilemanager.dto.modification.ModificationTerminalDto;
import com.tms.profilemanager.dto.patch.modification.ModificationTerminalPatchDto;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/modification-terminal")
@Tag(name = "Modification terminal", description = "Модификации терминала")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ModificationTerminalController extends
        AbstractCrudController<ModificationTerminal, ModificationTerminalDto, ModificationTerminalCreateDto, ModificationTerminalPatchDto, ModificationTerminalFilterDto, ModificationTerminalSort> {

    public ModificationTerminalController(ModificationTerminalService service, ModificationTerminalMapper mapper) {
        super(service, mapper);
    }
}
