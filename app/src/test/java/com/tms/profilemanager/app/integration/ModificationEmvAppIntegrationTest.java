package com.tms.profilemanager.app.integration;

import com.tms.profilemanager.dto.modification.ModificationEmvAppDto;
import com.tms.profilemanager.dto.create.modification.ModificationEmvAppCreateDto;
import com.tms.profilemanager.dto.enums.TerminalType;
import com.tms.profilemanager.dto.patch.modification.ModificationEmvAppPatchDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.apache.commons.lang3.StringUtils.repeat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class ModificationEmvAppIntegrationTest extends AbstractIntegrationTest {

    Long preparedPaymentSystemId;
    Long preparedTerminalGroupId;
    Long modificationId;
    Long emvAppId;
    Long preparedContactLimitId;
    Long preparedContactlessLimitId;
    Long preparedGeneralLimitId;

    @BeforeEach
    public void beforeEach() {
        databaseCleaner.clearDatabaseAndExecuteJavaMigrations();
        preparedTerminalGroupId = terminalGroupGenerator.createAndSave(1).getId();
        preparedContactLimitId = contactLimitGenerator.createAndSave(1).getId();
        preparedContactlessLimitId = contactlessLimitGenerator.createAndSave(1, List.of()).getId();
        preparedGeneralLimitId = generalLimitGenerator.createAndSave(1, preparedContactLimitId, preparedContactlessLimitId).getId();
        preparedPaymentSystemId = paymentSystemGenerator.createAndSave(1, preparedGeneralLimitId).getId();
        emvAppId = emvAppGenerator.createAndSave(1, preparedPaymentSystemId, TerminalType.ATTENDENT_11).getId();
        modificationId = modificationPaymentSystemGenerator.createAndSave(1, preparedPaymentSystemId, preparedTerminalGroupId, Set.of(preparedGeneralLimitId)).getId();
        requestSender.setCurrentUser(ADMIN);
    }

    @Test
    void getByIdTest() {
        ModificationEmvAppDto dto = modificationEmvAppGenerator.createAndSave(preparedPaymentSystemId, preparedTerminalGroupId, emvAppId);
        assertEquals(dto, modificationEmvAppTestClient.getById(dto.getId()));
    }

    @Test
    void createTest() {
        ModificationEmvAppCreateDto createDto = modificationEmvAppGenerator.buildCreateDto(preparedPaymentSystemId, preparedTerminalGroupId, emvAppId);

        createAndAssertBadRequest(createDto.toBuilder().emvAppsId(null).build());
        createAndAssertBadRequest(createDto.toBuilder().paymentSystemId(null).build());
        createAndAssertBadRequest(createDto.toBuilder().groupId(null).build());
        createAndAssertBadRequest(createDto.toBuilder().aid(repeat("a", 33)).build());
        createAndAssertBadRequest(createDto.toBuilder().ddol(repeat("a", 257)).build());
        createAndAssertBadRequest(createDto.toBuilder().tdol(repeat("a", 257)).build());
        createAndAssertBadRequest(createDto.toBuilder().tacDenial(repeat("a", 11)).build());
        createAndAssertBadRequest(createDto.toBuilder().tacDefault(repeat("a", 11)).build());
        createAndAssertBadRequest(createDto.toBuilder().tacOnline(repeat("a", 11)).build());
        createAndAssertBadRequest(createDto.toBuilder().terminalAdditionalCaps(repeat("a", 11)).build());
        createAndAssertBadRequest(createDto.toBuilder().terminalCaps(repeat("a", 7)).build());

        ModificationEmvAppDto dto = modificationEmvAppTestClient.create(createDto);
        assertCreateDtoMatchesDto(createDto, dto);
        assertCreateDtoMatchesDto(createDto, modificationEmvAppTestClient.getById(dto.getId()));
    }

    @Test
    void patchTest() {
        ModificationEmvAppDto dto = modificationEmvAppGenerator.createAndSave(preparedPaymentSystemId, preparedTerminalGroupId, emvAppId);
        ModificationEmvAppPatchDto patchDto = ModificationEmvAppPatchDto.builder().id(dto.getId()).build();

        patchAndAssertBadRequest(patchDto.toBuilder().emvAppsId(null).build());
        patchAndAssertBadRequest(patchDto.toBuilder().paymentSystemId(null).build());
        patchAndAssertBadRequest(patchDto.toBuilder().groupId(null).build());
        patchAndAssertBadRequest(patchDto.toBuilder().aid(Optional.of(repeat("a", 33))).build());
        patchAndAssertBadRequest(patchDto.toBuilder().ddol(Optional.of(repeat("a", 257))).build());
        patchAndAssertBadRequest(patchDto.toBuilder().tdol(Optional.of(repeat("a", 257))).build());
        patchAndAssertBadRequest(patchDto.toBuilder().tacDefault(Optional.of(repeat("a", 11))).build());
        patchAndAssertBadRequest(patchDto.toBuilder().tacOnline(Optional.of(repeat("a", 11))).build());
        patchAndAssertBadRequest(patchDto.toBuilder().tacDenial(Optional.of(repeat("a", 11))).build());
        patchAndAssertBadRequest(patchDto.toBuilder().terminalAdditionalCaps(Optional.of(repeat("a", 11))).build());
        patchAndAssertBadRequest(patchDto.toBuilder().terminalCaps(Optional.of(repeat("a", 7))).build());

        ModificationEmvAppDto patch = modificationEmvAppTestClient.patch(patchDto.toBuilder()
                .emvAppsId(dto.getEmvAppsId())
                .paymentSystemId(preparedPaymentSystemId)
                .groupId(preparedTerminalGroupId)
                .aid(Optional.empty())
                .ddol(Optional.of("123123"))
                .build());
        assertNull(patch.getAid());
        assertEquals(patch, modificationEmvAppTestClient.getById(patch.getId()));
        assertEquals("123123", patch.getDdol());
    }


    private void assertCreateDtoMatchesDto(ModificationEmvAppCreateDto createDto, ModificationEmvAppDto dto) {
        assertEquals(createDto.getAid(), dto.getAid());
        assertEquals(createDto.getTdol(), dto.getTdol());
        assertEquals(createDto.getDdol(), dto.getDdol());
        assertEquals(createDto.getTacDefault(), dto.getTacDefault());
        assertEquals(createDto.getTacDenial(), dto.getTacDenial());
        assertEquals(createDto.getTacOnline(), dto.getTacOnline());
        assertEquals(createDto.getTerminalCaps(), dto.getTerminalCaps());
        assertEquals(createDto.getTerminalType(), dto.getTerminalType());
        assertEquals(createDto.getTerminalAdditionalCaps(), dto.getTerminalAdditionalCaps());
    }

    private void createAndAssertBadRequest(ModificationEmvAppCreateDto createDto) {
        assertThrowsResponseStatusException(() -> modificationEmvAppTestClient.create(createDto), HttpStatus.BAD_REQUEST);
    }

    private void patchAndAssertBadRequest(ModificationEmvAppPatchDto patchDto) {
        assertThrowsResponseStatusException(() -> modificationEmvAppTestClient.patch(patchDto), HttpStatus.BAD_REQUEST);
    }

}
