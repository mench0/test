package com.tms.profilemanager.app.integration;

import com.tms.profilemanager.app.service.DRLService;
import com.tms.profilemanager.dto.BaseDto;
import com.tms.profilemanager.dto.DRLDto;
import com.tms.profilemanager.dto.create.DRLCreateDto;
import com.tms.profilemanager.dto.filter.DRLFilterDto;
import com.tms.profilemanager.dto.patch.DRLPatchDto;
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

import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.toList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DRLIntegrationTest extends AbstractIntegrationTest {

    @Autowired DRLService drlService;

    Long preparedContactlessLimitId;

    @BeforeEach
    public void beforeEach() {
        databaseCleaner.clearDatabaseAndExecuteJavaMigrations();
        preparedContactlessLimitId = contactlessLimitGenerator.createAndSave(1, List.of()).getId();
        requestSender.setCurrentUser(ADMIN);
    }

    @Test
    void getByIdTest() {
        DRLDto dto = drlGenerator.createAndSave(1, preparedContactlessLimitId);
        assertEquals(dto, drlTestClient.getById(dto.getId()));
    }

    @Test
    void searchTest() {
        Long contactlessLimit2Id = contactlessLimitGenerator.createAndSave(2, List.of()).getId();
        drlGenerator.createAndSave(1, preparedContactlessLimitId);
        drlGenerator.createAndSave(2, contactlessLimit2Id);
        drlGenerator.createAndSave(3, contactlessLimit2Id);

        DRLFilterDto filterDto = new DRLFilterDto();
        List<Long> ids = drlTestClient.search(filterDto).stream().map(BaseDto::getId).collect(toList());
        assertEquals(3, ids.size());
        filterDto.setIds(new HashSet<>(ids.subList(0, 2)));
        assertEquals(2, drlTestClient.search(filterDto).getTotalElements());

        filterDto.setIds(null);
        filterDto.setContactlessLimitIds(Set.of(contactlessLimit2Id));
        assertEquals(2, drlTestClient.search(filterDto).getTotalElements());
    }

    @Test
    void createTest() {
        DRLCreateDto createDto = drlGenerator.buildCreateDto(1, preparedContactlessLimitId);

        createAndAssertBadRequest(createDto.toBuilder().index(null).build());

        createAndAssertBadRequest(createDto.toBuilder().operationLimit(null).build());

        createAndAssertBadRequest(createDto.toBuilder().floorLimit(null).build());

        createAndAssertBadRequest(createDto.toBuilder().noCvmLimit(null).build());

        createAndAssertBadRequest(createDto.toBuilder().contactlessLimitId(-1L).build());

        DRLDto dto = drlTestClient.create(createDto);
        assertCreateDtoMatchesDto(createDto, dto);
        assertCreateDtoMatchesDto(createDto, drlTestClient.getById(dto.getId()));
    }

    @Test
    void patchTest() {
        DRLDto dto = drlGenerator.createAndSave(1, preparedContactlessLimitId);
        DRLPatchDto patchDto = DRLPatchDto.builder().id(dto.getId()).build();

        patchAndAssertBadRequest(patchDto.toBuilder().index(Optional.empty()).build());
        patchAndAssertOk(dto, dto.getIndex() + 1, dto::setIndex, p -> p::setIndex);

        patchAndAssertBadRequest(patchDto.toBuilder().operationLimit(Optional.empty()).build());
        patchAndAssertOk(dto, dto.getOperationLimit() + 1, dto::setOperationLimit, p -> p::setOperationLimit);

        patchAndAssertBadRequest(patchDto.toBuilder().floorLimit(Optional.empty()).build());
        patchAndAssertOk(dto, dto.getFloorLimit() + 1, dto::setFloorLimit, p -> p::setFloorLimit);

        patchAndAssertBadRequest(patchDto.toBuilder().noCvmLimit(Optional.empty()).build());
        patchAndAssertOk(dto, dto.getNoCvmLimit() + 1, dto::setNoCvmLimit, p -> p::setNoCvmLimit);

        patchAndAssertBadRequest(patchDto.toBuilder().contactlessLimitId(Optional.of(-1L)).build());
        Long newContactlessLimitId = contactlessLimitGenerator.createAndSave(2, List.of()).getId();
        patchAndAssertOk(dto, newContactlessLimitId, dto::setContactlessLimitId, p -> p::setContactlessLimitId);
        patchAndAssertOk(dto, null, dto::setContactlessLimitId, p -> p::setContactlessLimitId);
    }

    @Test
    void deleteByIdTest() {
        DRLDto dto = drlGenerator.createAndSave(1, preparedContactlessLimitId);
        assertNotNull(drlTestClient.getById(dto.getId()).getId());
        drlTestClient.deleteById(dto.getId());
        drlTestClient.assertNotExists(dto.getId());
    }

    @Test
    void archiveAndRestoreByIdTest() {
        DRLDto dto = drlGenerator.createAndSave(1, preparedContactlessLimitId);
        assertFalse(drlTestClient.getById(dto.getId()).getArchived());

        drlTestClient.archiveById(dto.getId());
        assertTrue(drlTestClient.getById(dto.getId()).getArchived());
        securityManager.runAsSystemUser(() -> assertNotNull(drlService.getByIdOrElseThrow(dto.getId()).getLastArchivedAt()));

        drlTestClient.restoreById(dto.getId());
        assertFalse(drlTestClient.getById(dto.getId()).getArchived());
        securityManager.runAsSystemUser(() -> assertNotNull(drlService.getByIdOrElseThrow(dto.getId()).getLastRestoredAt()));
    }

    private void createAndAssertBadRequest(DRLCreateDto createDto) {
        assertThrowsResponseStatusException(() -> drlTestClient.create(createDto), HttpStatus.BAD_REQUEST);
    }

    private void assertCreateDtoMatchesDto(DRLCreateDto createDto, DRLDto dto) {
        assertEquals(createDto.getIndex(), dto.getIndex());
        assertEquals(createDto.getOperationLimit(), dto.getOperationLimit());
        assertEquals(createDto.getFloorLimit(), dto.getFloorLimit());
        assertEquals(createDto.getNoCvmLimit(), dto.getNoCvmLimit());
        assertEquals(createDto.getContactlessLimitId(), dto.getContactlessLimitId());
    }

    private <T> void patchAndAssertOk(DRLDto dto,
                                      T newValue,
                                      Consumer<T> dtoSetter,
                                      Function<DRLPatchDto, Consumer<Optional<T>>> patchDtoSetterSupplier) {
        assertEquals(dto, drlTestClient.getById(dto.getId()));
        dtoSetter.accept(newValue);
        assertNotEquals(dto, drlTestClient.getById(dto.getId()));
        DRLPatchDto patchDto = DRLPatchDto.builder().id(dto.getId()).build();
        patchDtoSetterSupplier.apply(patchDto).accept(ofNullable(newValue));
        assertEquals(dto, drlTestClient.patch(patchDto));
        assertEquals(dto, drlTestClient.getById(dto.getId()));
    }

    private void patchAndAssertBadRequest(DRLPatchDto patchDto) {
        assertThrowsResponseStatusException(() -> drlTestClient.patch(patchDto), HttpStatus.BAD_REQUEST);
    }
}
