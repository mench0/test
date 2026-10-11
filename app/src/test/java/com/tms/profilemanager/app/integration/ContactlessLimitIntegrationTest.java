package com.tms.profilemanager.app.integration;

import com.neovisionaries.i18n.CurrencyCode;
import com.tms.profilemanager.app.service.ContactlessLimitService;
import com.tms.profilemanager.app.service.DRLService;
import com.tms.profilemanager.dto.BaseDto;
import com.tms.profilemanager.dto.ContactlessLimitDto;
import com.tms.profilemanager.dto.DRLDto;
import com.tms.profilemanager.dto.create.limit.ContactlessLimitCreateDto;
import com.tms.profilemanager.dto.CurrencyDto;
import com.tms.profilemanager.dto.filter.ContactlessLimitFilterDto;
import com.tms.profilemanager.dto.patch.ContactlessLimitPatchDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.toList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ContactlessLimitIntegrationTest extends AbstractIntegrationTest {

    @Autowired DRLService drlService;
    @Autowired ContactlessLimitService contactlessLimitService;

    List<Long> preparedDRLIds;

    @BeforeEach
    public void beforeEach() {
        databaseCleaner.clearDatabaseAndExecuteJavaMigrations();
        preparedDRLIds = new ArrayList<>();
        IntStream.of(1, 2, 3).forEach(number ->
                preparedDRLIds.add(drlGenerator.createAndSave(number, null).getId()));
        requestSender.setCurrentUser(ADMIN);
    }

    @Test
    void getByIdTest() {
        ContactlessLimitDto dto = contactlessLimitGenerator.createAndSave(1, preparedDRLIds);
        assertEquals(dto, contactlessLimitTestClient.getById(dto.getId()));
    }

    @Test
    void searchTest() {
        IntStream.of(1, 2, 3)
                .forEach(number -> contactlessLimitGenerator.createAndSave(number, preparedDRLIds));
        ContactlessLimitFilterDto filterDto = new ContactlessLimitFilterDto();
        List<Long> ids = contactlessLimitTestClient.search(filterDto).stream().map(BaseDto::getId).collect(toList());
        assertEquals(3, ids.size());
        filterDto.setIds(new HashSet<>(ids.subList(0, 2)));
        assertEquals(2, contactlessLimitTestClient.search(filterDto).getTotalElements());
    }

    @Test
    void createTest() {
        ContactlessLimitCreateDto createDto = contactlessLimitGenerator.buildCreateDto(1, preparedDRLIds);

        createAndAssertBadRequest(createDto.toBuilder().currency(null).build());

        createAndAssertBadRequest(createDto.toBuilder().floorLimit(null).build());

        createAndAssertBadRequest(createDto.toBuilder().noCvmLimit(null).build());

        createAndAssertBadRequest(createDto.toBuilder().onDeviceCvmLimit(null).build());

        createAndAssertBadRequest(createDto.toBuilder().noDeviceCvmLimit(null).build());

        createAndAssertBadRequest(createDto.toBuilder().maxAmount(null).build());

        createAndAssertBadRequest(createDto.toBuilder().maxAmount(-1L).build());

        createAndAssertBadRequest(createDto.toBuilder().maxAmount(1000000000000L).build());

        ContactlessLimitDto dto = contactlessLimitTestClient.create(createDto);
        assertCreateDtoMatchesDto(createDto, dto);
        assertCreateDtoMatchesDto(createDto, contactlessLimitTestClient.getById(dto.getId()));
    }

    @Test
    void patchTest() {
        ContactlessLimitDto dto = contactlessLimitGenerator.createAndSave(1, preparedDRLIds);
        ContactlessLimitPatchDto patchDto = ContactlessLimitPatchDto.builder().id(dto.getId()).build();

        patchAndAssertBadRequest(patchDto.toBuilder().currency(Optional.empty()).build());
        patchAndAssertOk(dto, CurrencyCode.USD, c -> dto.setCurrency(CurrencyDto.of(c)), p -> p::setCurrency);

        patchAndAssertBadRequest(patchDto.toBuilder().floorLimit(Optional.empty()).build());
        patchAndAssertOk(dto, dto.getFloorLimit() + 1, dto::setFloorLimit, p -> p::setFloorLimit);

        patchAndAssertBadRequest(patchDto.toBuilder().noCvmLimit(Optional.empty()).build());
        patchAndAssertOk(dto, dto.getNoCvmLimit() + 1, dto::setNoCvmLimit, p -> p::setNoCvmLimit);

        patchAndAssertBadRequest(patchDto.toBuilder().onDeviceCvmLimit(Optional.empty()).build());
        patchAndAssertOk(dto, dto.getOnDeviceCvmLimit() + 1, dto::setOnDeviceCvmLimit, p -> p::setOnDeviceCvmLimit);

        patchAndAssertBadRequest(patchDto.toBuilder().noDeviceCvmLimit(Optional.empty()).build());
        patchAndAssertOk(dto, dto.getNoDeviceCvmLimit() + 1, dto::setNoDeviceCvmLimit, p -> p::setNoDeviceCvmLimit);

        patchAndAssertBadRequest(patchDto.toBuilder().maxAmount(Optional.empty()).build());
        patchAndAssertBadRequest(patchDto.toBuilder().maxAmount(Optional.of(-1L)).build());
        patchAndAssertBadRequest(patchDto.toBuilder().maxAmount(Optional.of(1000000000000L)).build());
        patchAndAssertOk(dto, dto.getMaxAmount() + 1, dto::setMaxAmount, p -> p::setMaxAmount);

        List<DRLDto> preparedDRLDtoList = new ArrayList<>(dto.getDrlList());
        patchAndAssertOk(dto, dto.getDrlList().subList(0, 1), dto::setDrlList, p -> drlList -> p.setDrlIdList(mapToIds(drlList)));
        patchAndAssertOk(dto, preparedDRLDtoList, dto::setDrlList, p -> drlList -> p.setDrlIdList(mapToIds(drlList)));
    }

    @Test
    void deleteByIdTest() {
        ContactlessLimitDto dto = contactlessLimitGenerator.createAndSave(1, preparedDRLIds);
        assertNotNull(contactlessLimitTestClient.getById(dto.getId()).getId());
        securityManager.runAsSystemUser(() -> preparedDRLIds.forEach(drlService::delete));
        contactlessLimitTestClient.deleteById(dto.getId());
        contactlessLimitTestClient.assertNotExists(dto.getId());
    }

    @Test
    void archiveAndRestoreByIdTest() {
        ContactlessLimitDto dto = contactlessLimitGenerator.createAndSave(1, preparedDRLIds);
        assertFalse(contactlessLimitTestClient.getById(dto.getId()).getArchived());

        contactlessLimitTestClient.archiveById(dto.getId());
        assertTrue(contactlessLimitTestClient.getById(dto.getId()).getArchived());
        securityManager.runAsSystemUser(() -> assertNotNull(contactlessLimitService.getByIdOrElseThrow(dto.getId()).getLastArchivedAt()));

        contactlessLimitTestClient.restoreById(dto.getId());
        assertFalse(contactlessLimitTestClient.getById(dto.getId()).getArchived());
        securityManager.runAsSystemUser(() -> assertNotNull(contactlessLimitService.getByIdOrElseThrow(dto.getId()).getLastRestoredAt()));
    }

    private void createAndAssertBadRequest(ContactlessLimitCreateDto createDto) {
        assertThrowsResponseStatusException(() -> contactlessLimitTestClient.create(createDto), HttpStatus.BAD_REQUEST);
    }

    private void assertCreateDtoMatchesDto(ContactlessLimitCreateDto createDto, ContactlessLimitDto dto) {
        assertEquals(createDto.getCurrency().getNumeric(), dto.getCurrency().getNumericCode());
        assertEquals(createDto.getFloorLimit(), dto.getFloorLimit());
        assertEquals(createDto.getNoCvmLimit(), dto.getNoCvmLimit());
        assertEquals(createDto.getOnDeviceCvmLimit(), dto.getOnDeviceCvmLimit());
        assertEquals(createDto.getNoDeviceCvmLimit(), dto.getNoDeviceCvmLimit());
        assertEquals(createDto.getDrlIdList().size(), dto.getDrlList().size());
        mapToIds(dto.getDrlList()).forEach(id -> assertTrue(createDto.getDrlIdList().contains(id)));
    }

    private <T> void patchAndAssertOk(ContactlessLimitDto dto,
                                      T newValue,
                                      Consumer<T> dtoSetter,
                                      Function<ContactlessLimitPatchDto, Consumer<Optional<T>>> patchDtoSetterSupplier) {
        assertEquals(dto, contactlessLimitTestClient.getById(dto.getId()));
        dtoSetter.accept(newValue);
        assertNotEquals(dto, contactlessLimitTestClient.getById(dto.getId()));
        ContactlessLimitPatchDto patchDto = ContactlessLimitPatchDto.builder().id(dto.getId()).build();
        patchDtoSetterSupplier.apply(patchDto).accept(Optional.of(newValue));
        assertEquals(dto, contactlessLimitTestClient.patch(patchDto));
        assertEquals(dto, contactlessLimitTestClient.getById(dto.getId()));
    }

    private void patchAndAssertBadRequest(ContactlessLimitPatchDto patchDto) {
        assertThrowsResponseStatusException(() -> contactlessLimitTestClient.patch(patchDto), HttpStatus.BAD_REQUEST);
    }
}
