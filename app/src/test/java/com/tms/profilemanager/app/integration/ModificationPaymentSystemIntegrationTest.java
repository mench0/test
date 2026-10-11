package com.tms.profilemanager.app.integration;

import com.tms.profilemanager.dto.BaseDto;
import com.tms.profilemanager.dto.TerminalGroupDto;
import com.tms.profilemanager.dto.create.modification.ModificationPaymentSystemCreateDto;
import com.tms.profilemanager.dto.filter.ModificationPaymentSystemFilterDto;
import com.tms.profilemanager.dto.modification.ModificationPaymentSystemDto;
import com.tms.profilemanager.dto.patch.modification.ModificationPaymentSystemPatchDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.toList;
import static org.apache.commons.lang3.StringUtils.repeat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

public class ModificationPaymentSystemIntegrationTest extends AbstractIntegrationTest {

    Long preparedPaymentSystemId;
    Long preparedContactLimitId;
    Long preparedContactlessLimitId;
    Long preparedGeneralLimitId;
    Long preparedTerminalGroupId;

    @BeforeEach
    public void beforeEach() {
        databaseCleaner.clearDatabaseAndExecuteJavaMigrations();
        preparedTerminalGroupId = terminalGroupGenerator.createAndSave(1).getId();
        preparedContactLimitId = contactLimitGenerator.createAndSave(1).getId();
        preparedContactlessLimitId = contactlessLimitGenerator.createAndSave(1, List.of()).getId();
        preparedGeneralLimitId = generalLimitGenerator.createAndSave(1, preparedContactLimitId, preparedContactlessLimitId).getId();
        preparedPaymentSystemId = paymentSystemGenerator.createAndSave(1, preparedGeneralLimitId).getId();
        requestSender.setCurrentUser(ADMIN);
    }

    @Test
    void getByIdTest() {
        ModificationPaymentSystemDto dto = modificationPaymentSystemGenerator.createAndSave(1, preparedPaymentSystemId, preparedTerminalGroupId, Set.of(preparedGeneralLimitId));
        assertEquals(dto, modificationPaymentSystemTestClient.getById(dto.getId()));
    }

    @Test
    void searchTest() {
        IntStream.range(2, 5).forEach(it -> {
            TerminalGroupDto groupDto = terminalGroupGenerator.createAndSave(it);
            Long groupId = groupDto.getId();
            modificationPaymentSystemGenerator.createAndSave(it, preparedPaymentSystemId, groupId, Set.of(preparedGeneralLimitId));
        });
        ModificationPaymentSystemFilterDto filterDto = new ModificationPaymentSystemFilterDto();
        List<Long> ids = modificationPaymentSystemTestClient.search(filterDto).stream().map(BaseDto::getId).collect(toList());
        assertEquals(3, ids.size());
        filterDto.setIds(new HashSet<>(ids.subList(0, 2)));
        assertEquals(2, modificationPaymentSystemTestClient.search(filterDto).getTotalElements());
    }

    @Test
    void createTest() {
        ModificationPaymentSystemCreateDto createDto = modificationPaymentSystemGenerator.buildCreateDto(1, preparedPaymentSystemId, preparedTerminalGroupId, Set.of(preparedGeneralLimitId));
        Long preparedGeneralLimitId1 = generalLimitGenerator.createAndSave(2, preparedContactLimitId, preparedContactlessLimitId).getId();

        createAndAssertBadRequest(createDto.toBuilder().paymentSystemId(null).build());
        createAndAssertBadRequest(createDto.toBuilder().groupId(null).build());
        createAndAssertBadRequest(createDto.toBuilder().avn(repeat("*", 5)).build());
        createAndAssertBadRequest(createDto.toBuilder().contactlessAvn(repeat("*", 5)).build());
        createAndAssertBadRequest(createDto.toBuilder().limits(Set.of(preparedGeneralLimitId, preparedGeneralLimitId1)).build());

        ModificationPaymentSystemDto dto = modificationPaymentSystemTestClient.create(createDto);
        createAndAssertBadRequest(createDto.toBuilder().paymentSystemId(preparedPaymentSystemId).groupId(createDto.getGroupId()).build());

        Long preparedPaymentSystemId1 = paymentSystemGenerator.createAndSave(2, preparedGeneralLimitId).getId();
        assertNotNull(modificationPaymentSystemGenerator.createAndSave(2, preparedPaymentSystemId1, preparedTerminalGroupId, Set.of(preparedGeneralLimitId)));

        assertCreateDtoMatchesDto(createDto, dto);
        assertCreateDtoMatchesDto(createDto, modificationPaymentSystemTestClient.getById(dto.getId()));
    }

    @Test
    void patchTest() {
        ModificationPaymentSystemDto dto = modificationPaymentSystemGenerator.createAndSave(1, preparedPaymentSystemId, preparedTerminalGroupId, Set.of(preparedGeneralLimitId));
        ModificationPaymentSystemPatchDto patchDto = ModificationPaymentSystemPatchDto.builder().id(dto.getId()).build();

        patchAndAssertBadRequest(patchDto.toBuilder().avn(Optional.of(repeat("a", 5))).build());
        patchAndAssertBadRequest(patchDto.toBuilder().contactlessAvn(Optional.of(repeat("a", 5))).build());

        Long newGeneralLimitId = generalLimitGenerator.createAndSave(2, preparedContactLimitId, preparedContactlessLimitId).getId();

        ModificationPaymentSystemDto patch = modificationPaymentSystemTestClient.patch(patchDto.toBuilder()
                .groupId(preparedTerminalGroupId)
                .paymentSystemId(preparedPaymentSystemId)
                .avn(Optional.empty())
                .contactlessAvn(Optional.of(repeat("a", 4)))
                .limits(Set.of(newGeneralLimitId))
                .build());
        assertNull(patch.getAvn());
        assertEquals(1, patch.getLimits().size());
        assertEquals(patch, modificationPaymentSystemTestClient.getById(patch.getId()));
        assertEquals(repeat("a", 4), patch.getContactlessAvn());
    }

    private void assertCreateDtoMatchesDto(ModificationPaymentSystemCreateDto createDto, ModificationPaymentSystemDto dto) {
        assertEquals(createDto.getPaymentSystemId(), dto.getPaymentSystemId());
        assertEquals(createDto.getAvn(), dto.getAvn());
        assertEquals(createDto.getContactlessAvn(), dto.getContactlessAvn());
        assertEquals(createDto.getGroupId(), dto.getGroupId());

        assertEquals(createDto.getLimits(), dto.getLimits().stream()
                .map(ModificationPaymentSystemDto.ModificationGeneralLimitDto::getId)
                .collect(Collectors.toSet()));

        assertEquals(createDto.getContactlessAvn(), dto.getContactlessAvn());
    }

    private void createAndAssertBadRequest(ModificationPaymentSystemCreateDto createDto) {
        assertThrowsResponseStatusException(() -> modificationPaymentSystemTestClient.create(createDto), HttpStatus.BAD_REQUEST);
    }

    private void patchAndAssertBadRequest(ModificationPaymentSystemPatchDto patchDto) {
        assertThrowsResponseStatusException(() -> modificationPaymentSystemTestClient.patch(patchDto), HttpStatus.BAD_REQUEST);
    }


}
