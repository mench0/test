package com.tms.profilemanager.app.integration;

import com.tms.profilemanager.app.service.TerminalGroupService;
import com.tms.profilemanager.dto.BaseDto;
import com.tms.profilemanager.dto.TerminalDto;
import com.tms.profilemanager.dto.TerminalGroupDto;
import com.tms.profilemanager.dto.VendorDto;
import com.tms.profilemanager.dto.VendorModelDto;
import com.tms.profilemanager.dto.create.TerminalCreateDto;
import com.tms.profilemanager.dto.create.TerminalGroupCreateDto;
import com.tms.profilemanager.dto.filter.TerminalGroupFilterDto;
import com.tms.profilemanager.dto.filter.VendorFilterDto;
import com.tms.profilemanager.dto.patch.TerminalGroupPatchDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;

import static java.util.stream.Collectors.toList;
import static org.apache.commons.lang3.StringUtils.repeat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TerminalGroupIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    TerminalGroupService terminalGroupService;

    private Long preparedMerchantId;
    private Long preparedPaymentSystemId;
    private Long preparedAcquirerId;
    private Long preparedContactLimitId;
    private Long preparedContactlessLimitId;
    private Long preparedGeneralLimitId;
    private String preparedPaymentTypeValue;

    @BeforeEach
    public void beforeEach() {
        databaseCleaner.clearDatabaseAndExecuteJavaMigrations();
        preparedMerchantId = merchantGenerator.createAndSave(1).getId();
        preparedContactLimitId = contactLimitGenerator.createAndSave(1).getId();
        preparedContactlessLimitId = contactlessLimitGenerator.createAndSave(1, List.of()).getId();
        preparedGeneralLimitId = generalLimitGenerator.createAndSave(1, preparedContactLimitId, preparedContactlessLimitId).getId();
        preparedPaymentSystemId = paymentSystemGenerator.createAndSave(1, preparedGeneralLimitId).getId();
        preparedAcquirerId = acquirerGenerator.createAndSave(1, Set.of(preparedPaymentSystemId)).getId();
        preparedPaymentTypeValue = paymentTypeGenerator.createAndSave(1).getPaymentValue();
        requestSender.setCurrentUser(ADMIN);
    }

    @Test
    void getByIdTest() {
        TerminalGroupDto dto = terminalGroupGenerator.createAndSave(1);
        assertEquals(dto, terminalGroupTestClient.getById(dto.getId()));
    }

    @Test
    void getByIdWithParentTest() {
        TerminalGroupDto parentDto1 = terminalGroupGenerator.createAndSave(1);
        TerminalGroupDto childDto1 = terminalGroupGenerator.createAndSaveWithParent(3, parentDto1.getId());
        TerminalGroupDto childDto2 = terminalGroupGenerator.createAndSaveWithParent(4, childDto1.getId());

        VendorFilterDto filterDto = new VendorFilterDto();
        VendorDto vendorDto = vendorTestClient.search(filterDto).getContent().get(0);
        VendorModelDto vendorModelDto = vendorDto.getModels().stream().findFirst().get();
        TerminalCreateDto createDto = terminalGenerator.buildCreateDto(1, preparedMerchantId, Set.of(preparedAcquirerId), vendorModelDto.getId(), preparedPaymentTypeValue);
        createDto.setGroupId(childDto2.getId());
        terminalTestClient.create(createDto);

        TerminalGroupDto dto1 = terminalGroupTestClient.getById(parentDto1.getId());

        assertEquals(1, dto1.getChildren().size());
        assertEquals(1, dto1.getChildren().get(0).getChildren().size());
        assertEquals(1, dto1.getTerminalsCount());
    }

    @Test
    void searchTest() {
        TerminalGroupDto parent1 = terminalGroupGenerator.createAndSave(1);
        terminalGroupGenerator.createAndSave(2);
        terminalGroupGenerator.createAndSave(3);

        TerminalGroupDto childDto1 = terminalGroupGenerator.createAndSaveWithParent(4, parent1.getId());
        TerminalGroupDto childDto2 = terminalGroupGenerator.createAndSaveWithParent(5, childDto1.getId());
        terminalGroupGenerator.createAndSaveWithParent(6, childDto1.getId());

        TerminalGroupDto parentAfterSaving1 = terminalGroupTestClient.getById(parent1.getId());
        TerminalGroupDto parentAfterSaving2 = terminalGroupTestClient.getById(childDto1.getId());
        TerminalGroupDto parentAfterSaving3 = terminalGroupTestClient.getById(childDto2.getId());

        assertEquals(1, parentAfterSaving1.getChildren().size());
        assertEquals(2, parentAfterSaving2.getChildren().size());
        assertNull(parentAfterSaving3.getChildren());

        TerminalGroupFilterDto filterDto = new TerminalGroupFilterDto();
        List<Long> ids = terminalGroupTestClient.search(filterDto).stream().map(BaseDto::getId).collect(toList());
        assertEquals(3, ids.size());
        filterDto.setIds(new HashSet<>(ids.subList(0, 2)));
        assertEquals(2, terminalGroupTestClient.search(filterDto).getTotalElements());
    }

    @Test
    void createTest() {
        TerminalGroupCreateDto createDto = terminalGroupGenerator.buildCreateDto(1);

        createAndAssertBadRequest(createDto.toBuilder().name(null).build());
        createAndAssertBadRequest(createDto.toBuilder().name("").build());
        createAndAssertBadRequest(createDto.toBuilder().name(repeat("a", 256)).build());

        TerminalGroupDto dto = terminalGroupTestClient.create(createDto);
        assertCreateDtoMatchesDto(createDto, dto);
        assertCreateDtoMatchesDto(createDto, terminalGroupTestClient.getById(dto.getId()));
    }

    @Test
    void patchTest() {
        TerminalGroupDto dto = terminalGroupGenerator.createAndSave(1);
        TerminalGroupPatchDto patchDto = TerminalGroupPatchDto.builder().id(dto.getId()).build();

        patchAndAssertBadRequest(patchDto.toBuilder().name(Optional.empty()).build());
        patchAndAssertBadRequest(patchDto.toBuilder().name(Optional.of("")).build());
        patchAndAssertBadRequest(patchDto.toBuilder().name(Optional.of(repeat("a", 256))).build());
        patchAndAssertOk(dto, repeat("a", 99), dto::setName, p -> p::setName);
    }

    @Test
    void deleteByIdTest() {
        TerminalGroupDto parent = terminalGroupGenerator.createAndSave(1);
        TerminalGroupDto child1 = terminalGroupGenerator.createAndSaveWithParent(2, parent.getId());
        TerminalGroupDto child2= terminalGroupGenerator.createAndSaveWithParent(3, child1.getId());

        VendorFilterDto filterDto = new VendorFilterDto();
        VendorDto vendorDto = vendorTestClient.search(filterDto).getContent().get(0);
        VendorModelDto vendorModelDto = vendorDto.getModels().stream().findFirst().get();
        TerminalCreateDto createDto = terminalGenerator.buildCreateDto(1, preparedMerchantId, Set.of(preparedAcquirerId), vendorModelDto.getId(), preparedPaymentTypeValue);
        createDto.setGroupId(parent.getId());
        TerminalDto savedTerminal = terminalTestClient.create(createDto);

        TerminalDto terminalDto = terminalTestClient.getById(savedTerminal.getId());
        assertEquals(parent.getId(), terminalDto.getGroup().getId());

        assertNotNull(terminalGroupTestClient.getById(parent.getId()).getId());
        assertNotNull(terminalGroupTestClient.getById(child1.getId()).getId());
        terminalGroupTestClient.deleteById(child1.getId());
        terminalGroupTestClient.assertNotExists(child1.getId());
        terminalGroupTestClient.assertNotExists(child2.getId());
        TerminalDto terminalDtoAfterDeletingSubgroups = terminalTestClient.getById(savedTerminal.getId());
        TerminalGroupDto groupDto = terminalGroupTestClient.getById(parent.getId());
        assertEquals(terminalDtoAfterDeletingSubgroups.getGroup().getId(), groupDto.getId());
        assertEquals(terminalDtoAfterDeletingSubgroups.getGroup().getName(), groupDto.getName());
        assertNull(terminalGroupTestClient.getById(parent.getId()).getChildren());

        assertNotNull(terminalTestClient.getById(terminalDto.getId()).getGroup());
    }

    @Test
    void archiveAndRestoreByIdTest() {
        TerminalGroupDto dto = terminalGroupGenerator.createAndSave(1);
        assertFalse(terminalGroupTestClient.getById(dto.getId()).getArchived());

        terminalGroupTestClient.archiveById(dto.getId());
        assertTrue(terminalGroupTestClient.getById(dto.getId()).getArchived());
        securityManager.runAsSystemUser(() -> assertNotNull(terminalGroupService.getByIdOrElseThrow(dto.getId()).getLastArchivedAt()));

        terminalGroupTestClient.restoreById(dto.getId());
        assertFalse(terminalGroupTestClient.getById(dto.getId()).getArchived());
        securityManager.runAsSystemUser(() -> assertNotNull(terminalGroupService.getByIdOrElseThrow(dto.getId()).getLastRestoredAt()));
    }

    private void createAndAssertBadRequest(TerminalGroupCreateDto createDto) {
        assertThrowsResponseStatusException(() -> terminalGroupTestClient.create(createDto), HttpStatus.BAD_REQUEST);
    }

    private void assertCreateDtoMatchesDto(TerminalGroupCreateDto createDto, TerminalGroupDto dto) {
        assertEquals(createDto.getName(), dto.getName());
    }

    private <T> void patchAndAssertOk(TerminalGroupDto dto,
                                      T newValue,
                                      Consumer<T> dtoSetter,
                                      Function<TerminalGroupPatchDto, Consumer<Optional<T>>> patchDtoSetterSupplier) {
        assertEquals(dto, terminalGroupTestClient.getById(dto.getId()));
        dtoSetter.accept(newValue);
        assertNotEquals(dto, terminalGroupTestClient.getById(dto.getId()));
        TerminalGroupPatchDto patchDto = TerminalGroupPatchDto.builder().id(dto.getId()).build();
        patchDtoSetterSupplier.apply(patchDto).accept(Optional.of(newValue));
        assertEquals(dto, terminalGroupTestClient.patch(patchDto));
        assertEquals(dto, terminalGroupTestClient.getById(dto.getId()));
    }

    private void patchAndAssertBadRequest(TerminalGroupPatchDto patchDto) {
        assertThrowsResponseStatusException(() -> terminalGroupTestClient.patch(patchDto), HttpStatus.BAD_REQUEST);
    }
}
