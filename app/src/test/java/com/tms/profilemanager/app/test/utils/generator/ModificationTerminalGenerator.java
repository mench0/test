package com.tms.profilemanager.app.test.utils.generator;

import com.neovisionaries.i18n.CountryCode;
import com.tms.profilemanager.app.config.security.RunAsSystemUser;
import com.tms.profilemanager.app.controller.ModificationTerminalController;
import com.tms.profilemanager.dto.create.modification.ModificationTerminalCreateDto;
import com.tms.profilemanager.dto.modification.ModificationTerminalDto;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Transactional
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ModificationTerminalGenerator {

    ModificationTerminalController modificationTerminalController;

    @RunAsSystemUser
    public ModificationTerminalDto createAndSave(Long groupId) {
        return modificationTerminalController.create(buildCreateDto(groupId));
    }

    public ModificationTerminalCreateDto buildCreateDto(Long groupId) {
        return ModificationTerminalCreateDto.builder()
                .country(CountryCode.AR.getName())
                .servicePin("12346")
                .groupId(groupId)
                .build();
    }
}
