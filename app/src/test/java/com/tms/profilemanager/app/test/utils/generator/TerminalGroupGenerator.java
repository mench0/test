package com.tms.profilemanager.app.test.utils.generator;

import com.tms.profilemanager.app.config.security.RunAsSystemUser;
import com.tms.profilemanager.app.controller.TerminalGroupController;
import com.tms.profilemanager.dto.TerminalGroupDto;
import com.tms.profilemanager.dto.create.TerminalGroupCreateDto;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;


@Component
@Transactional
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class TerminalGroupGenerator {

    TerminalGroupController terminalGroupController;

    @RunAsSystemUser
    public TerminalGroupDto createAndSave(int number) {
        return terminalGroupController.create(buildCreateDto(number));
    }

    @RunAsSystemUser
    public TerminalGroupDto createAndSaveWithParent(int number, Long parentId) {
        return terminalGroupController.create(buildCreateDtoToParent(number, parentId));
    }

    public TerminalGroupCreateDto buildCreateDto(int number) {
        return TerminalGroupCreateDto.builder()
                .name("name" + number)
                .build();
    }

    public TerminalGroupCreateDto buildCreateDtoToParent(int number, Long parentId) {
        return TerminalGroupCreateDto.builder()
                .name("name" + number)
                .parentId(parentId)
                .build();
    }
}
