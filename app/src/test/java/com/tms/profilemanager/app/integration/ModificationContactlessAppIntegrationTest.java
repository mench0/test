package com.tms.profilemanager.app.integration;

import com.tms.profilemanager.dto.create.modification.ModificationContactlessAppCreateDto;
import com.tms.profilemanager.dto.enums.TerminalType;
import com.tms.profilemanager.dto.modification.ModificationContactlessAppDto;
import com.tms.profilemanager.dto.patch.modification.ModificationContactlessAppPatchDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.apache.commons.lang3.StringUtils.repeat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class ModificationContactlessAppIntegrationTest extends AbstractIntegrationTest {

    Long preparedPaymentSystemId;
    Long preparedTerminalGroupId;
    Long modificationId;
    Long contactlessAppId;
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
        contactlessAppId = contactlessAppGenerator.createAndSave(1, preparedPaymentSystemId, TerminalType.ATTENDENT_11).getId();
        modificationId = modificationPaymentSystemGenerator.createAndSave(1, preparedPaymentSystemId, preparedTerminalGroupId, Set.of(preparedGeneralLimitId)).getId();
        requestSender.setCurrentUser(ADMIN);
    }

    @Test
    void getByIdTest() {
        ModificationContactlessAppDto dto = modificationContactlessAppGenerator.createAndSave(preparedPaymentSystemId, preparedTerminalGroupId, contactlessAppId);
        assertEquals(dto, modificationContactlessAppTestClient.getById(dto.getId()));
    }

    @Test
    void createTest() {
        ModificationContactlessAppCreateDto createDto = modificationContactlessAppGenerator.buildCreateDto(preparedPaymentSystemId, preparedTerminalGroupId, contactlessAppId);

        createAndAssertBadRequest(createDto.toBuilder().contactlessAppsId(null).build());
        createAndAssertBadRequest(createDto.toBuilder().paymentSystemId(null).build());
        createAndAssertBadRequest(createDto.toBuilder().groupId(null).build());
        createAndAssertBadRequest(createDto.toBuilder().aid(repeat("a", 33)).build());
        createAndAssertBadRequest(createDto.toBuilder().kernelConfiguration(repeat("a", 4)).build());
        createAndAssertBadRequest(createDto.toBuilder().kernelId(repeat("a", 3)).build());
        createAndAssertBadRequest(createDto.toBuilder().tacDenial(repeat("a", 11)).build());
        createAndAssertBadRequest(createDto.toBuilder().tacDefault(repeat("a", 11)).build());
        createAndAssertBadRequest(createDto.toBuilder().tacOnline(repeat("a", 11)).build());
        createAndAssertBadRequest(createDto.toBuilder().terminalAdditionalCaps(repeat("a", 11)).build());
        createAndAssertBadRequest(createDto.toBuilder().terminalCaps(repeat("a", 7)).build());
        createAndAssertBadRequest(createDto.toBuilder().terminalCapsCVMNotRequired(repeat("a", 7)).build());

        createDto.setPaymentSystemId(preparedPaymentSystemId);
        createDto.setGroupId(preparedTerminalGroupId);
        createDto.setContactlessAppsId(contactlessAppId);
        ModificationContactlessAppDto dto = modificationContactlessAppTestClient.create(createDto);
        assertCreateDtoMatchesDto(createDto, dto);
        assertCreateDtoMatchesDto(createDto, modificationContactlessAppTestClient.getById(dto.getId()));
    }

    @Test
    void patchTest() {
        ModificationContactlessAppDto dto = modificationContactlessAppGenerator.createAndSave(preparedPaymentSystemId, preparedTerminalGroupId, contactlessAppId);
        ModificationContactlessAppPatchDto patchDto = ModificationContactlessAppPatchDto.builder().id(dto.getId()).build();

        patchAndAssertBadRequest(patchDto.toBuilder().contactlessAppsId(null).build());
        patchAndAssertBadRequest(patchDto.toBuilder().paymentSystemId(null).build());
        patchAndAssertBadRequest(patchDto.toBuilder().groupId(null).build());
        patchAndAssertBadRequest(patchDto.toBuilder().contactlessAppsId(null).build());
        patchAndAssertBadRequest(patchDto.toBuilder().aid(Optional.of(repeat("a", 33))).build());
        patchAndAssertBadRequest(patchDto.toBuilder().kernelConfiguration(Optional.of(repeat("a", 4))).build());
        patchAndAssertBadRequest(patchDto.toBuilder().kernelId(Optional.of(repeat("a", 3))).build());
        patchAndAssertBadRequest(patchDto.toBuilder().tacDenial(Optional.of(repeat("a", 11))).build());
        patchAndAssertBadRequest(patchDto.toBuilder().tacDefault(Optional.of(repeat("a", 11))).build());
        patchAndAssertBadRequest(patchDto.toBuilder().tacOnline(Optional.of(repeat("a", 11))).build());
        patchAndAssertBadRequest(patchDto.toBuilder().terminalAdditionalCaps(Optional.of(repeat("a", 11))).build());
        patchAndAssertBadRequest(patchDto.toBuilder().terminalCaps(Optional.of(repeat("a", 7))).build());
        patchAndAssertBadRequest(patchDto.toBuilder().terminalCapsCVMNotRequired(Optional.of(repeat("a", 7))).build());

        ModificationContactlessAppDto patch = modificationContactlessAppTestClient.patch(patchDto.toBuilder()
                .contactlessAppsId(dto.getContactlessAppsId())
                .paymentSystemId(preparedPaymentSystemId)
                .groupId(preparedTerminalGroupId)
                .aid(Optional.empty())
                .tacDenial(Optional.of("123123"))
                .build());
        assertNull(patch.getAid());
        assertEquals(patch, modificationContactlessAppTestClient.getById(patch.getId()));
        assertEquals("123123", patch.getTacDenial());
    }


    private void assertCreateDtoMatchesDto(ModificationContactlessAppCreateDto createDto, ModificationContactlessAppDto dto) {
        assertEquals(createDto.getAid(), dto.getAid());
        assertEquals(createDto.getTags(), dto.getTags());
        assertEquals(createDto.getTacDefault(), dto.getTacDefault());
        assertEquals(createDto.getTacDenial(), dto.getTacDenial());
        assertEquals(createDto.getTacOnline(), dto.getTacOnline());
        assertEquals(createDto.getTerminalCaps(), dto.getTerminalCaps());
        assertEquals(createDto.getTerminalType(), dto.getTerminalType());
        assertEquals(createDto.getTerminalAdditionalCaps(), dto.getTerminalAdditionalCaps());
        assertEquals(createDto.getKernelConfiguration(), dto.getKernelConfiguration());
        assertEquals(createDto.getKernelId(), dto.getKernelId());
        assertEquals(createDto.getTerminalCapsCVMNotRequired(), dto.getTerminalCapsCVMNotRequired());
    }

    private void createAndAssertBadRequest(ModificationContactlessAppCreateDto createDto) {
        assertThrowsResponseStatusException(() -> modificationContactlessAppTestClient.create(createDto), HttpStatus.BAD_REQUEST);
    }

    private void patchAndAssertBadRequest(ModificationContactlessAppPatchDto patchDto) {
        assertThrowsResponseStatusException(() -> modificationContactlessAppTestClient.patch(patchDto), HttpStatus.BAD_REQUEST);
    }

}
