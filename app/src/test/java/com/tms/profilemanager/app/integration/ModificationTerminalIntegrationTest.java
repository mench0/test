package com.tms.profilemanager.app.integration;

import com.tms.profilemanager.dto.BaseDto;
import com.tms.profilemanager.dto.TerminalGroupDto;
import com.tms.profilemanager.dto.create.modification.ModificationTerminalCreateDto;
import com.tms.profilemanager.dto.filter.ModificationTerminalFilterDto;
import com.tms.profilemanager.dto.modification.ModificationTerminalDto;
import com.tms.profilemanager.dto.patch.modification.ModificationTerminalPatchDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.toList;
import static org.apache.commons.lang3.StringUtils.repeat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class ModificationTerminalIntegrationTest extends AbstractIntegrationTest {

    Long preparedTerminalGroupId;

    @BeforeEach
    public void beforeEach() {
        databaseCleaner.clearDatabaseAndExecuteJavaMigrations();
        preparedTerminalGroupId = terminalGroupGenerator.createAndSave(1).getId();
        requestSender.setCurrentUser(ADMIN);
    }

    @Test
    void getByIdTest() {
        ModificationTerminalDto dto = modificationTerminalGenerator.createAndSave(preparedTerminalGroupId);
        assertEquals(dto, modificationTerminalTestClient.getById(dto.getId()));
    }

    @Test
    void searchTest() {
        IntStream.range(2, 5).forEach(it -> {
            TerminalGroupDto groupDto = terminalGroupGenerator.createAndSave(it);
            Long groupId = groupDto.getId();
            modificationTerminalGenerator.createAndSave(groupId);
        });
        ModificationTerminalFilterDto filterDto = new ModificationTerminalFilterDto();
        List<Long> ids = modificationTerminalTestClient.search(filterDto).stream().map(BaseDto::getId).collect(toList());
        assertEquals(3, ids.size());
        filterDto.setIds(new HashSet<>(ids.subList(0, 2)));
        assertEquals(2, modificationTerminalTestClient.search(filterDto).getTotalElements());
    }

    @Test
    void createTest() {
        ModificationTerminalCreateDto createDto = modificationTerminalGenerator.buildCreateDto(preparedTerminalGroupId);

        createAndAssertBadRequest(createDto.toBuilder().servicePin(repeat("*", 11)).build());
        createAndAssertBadRequest(createDto.toBuilder().country(repeat("*", 101)).build());

        ModificationTerminalDto dto = modificationTerminalTestClient.create(createDto);
        assertCreateDtoMatchesDto(createDto, dto);
        assertCreateDtoMatchesDto(createDto, modificationTerminalTestClient.getById(dto.getId()));
    }

    @Test
    void patchTest() {
        ModificationTerminalDto dto = modificationTerminalGenerator.createAndSave(preparedTerminalGroupId);
        ModificationTerminalPatchDto patchDto = ModificationTerminalPatchDto.builder().id(dto.getId()).build();

        patchAndAssertBadRequest(patchDto.toBuilder().servicePin(Optional.of(repeat("*", 11))).build());
        patchAndAssertBadRequest(patchDto.toBuilder().country(Optional.of(repeat("*", 4))).build());

        ModificationTerminalDto patch = modificationTerminalTestClient.patch(patchDto.toBuilder()
                .groupId(preparedTerminalGroupId)
                .country(Optional.empty())
                .servicePin(Optional.of(repeat("a", 10))).build());
        assertNull(patch.getCountry());
        assertEquals(patch, modificationTerminalTestClient.getById(patch.getId()));
        assertEquals(repeat("a", 10), patch.getServicePin());
    }


    private void assertCreateDtoMatchesDto(ModificationTerminalCreateDto createDto, ModificationTerminalDto dto) {
        assertEquals(createDto.getCountry(), dto.getCountry());
        assertEquals(createDto.getServicePin(), dto.getServicePin());
        assertEquals(createDto.getGroupId(), dto.getGroupId());
    }

    private void createAndAssertBadRequest(ModificationTerminalCreateDto createDto) {
        assertThrowsResponseStatusException(() -> modificationTerminalTestClient.create(createDto), HttpStatus.BAD_REQUEST);
    }

    private void patchAndAssertBadRequest(ModificationTerminalPatchDto patchDto) {
        assertThrowsResponseStatusException(() -> modificationTerminalTestClient.patch(patchDto), HttpStatus.BAD_REQUEST);
    }

}
