package com.tms.profilemanager.app.integration;

import com.neovisionaries.i18n.CurrencyCode;
import com.tms.profilemanager.app.service.ContactLimitService;
import com.tms.profilemanager.dto.BaseDto;
import com.tms.profilemanager.dto.ContactLimitDto;
import com.tms.profilemanager.dto.create.limit.ContactLimitCreateDto;
import com.tms.profilemanager.dto.enums.AcquirerOperationType;
import com.tms.profilemanager.dto.CurrencyDto;
import com.tms.profilemanager.dto.filter.ContactLimitFilterDto;
import com.tms.profilemanager.dto.patch.ContactLimitPatchDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ContactLimitIntegrationTest extends AbstractIntegrationTest {

    @Autowired ContactLimitService contactLimitService;

    @BeforeEach
    public void beforeEach() {
        databaseCleaner.clearDatabaseAndExecuteJavaMigrations();
        requestSender.setCurrentUser(ADMIN);
    }

    @Test
    void getByIdTest() {
        ContactLimitDto dto = contactLimitGenerator.createAndSave(1);
        assertEquals(dto, contactLimitTestClient.getById(dto.getId()));
    }

    @Test
    void searchTest() {
        IntStream.of(1, 2, 3).forEach(number -> contactLimitGenerator.createAndSave(number));
        ContactLimitFilterDto filterDto = new ContactLimitFilterDto();
        List<Long> ids = contactLimitTestClient.search(filterDto).stream().map(BaseDto::getId).toList();
        assertEquals(3, ids.size());
        filterDto.setIds(new HashSet<>(ids.subList(0, 2)));
        assertEquals(2, contactLimitTestClient.search(filterDto).getTotalElements());
    }

    @Test
    void createTest() {
        ContactLimitCreateDto createDto = contactLimitGenerator.buildCreateDto(1);

        createAndAssertBadRequest(createDto.toBuilder().currency(null).build());

        createAndAssertBadRequest(createDto.toBuilder().threshold(null).build());

        createAndAssertBadRequest(createDto.toBuilder().targetPercent(null).build());

        createAndAssertBadRequest(createDto.toBuilder().maxTargetPercent(null).build());

        createAndAssertBadRequest(createDto.toBuilder().floorLimit(null).build());

        createAndAssertBadRequest(createDto.toBuilder().pinBypassAllowed(null).build());

        createAndAssertBadRequest(createDto.toBuilder().pinLimit(null).build());

        ContactLimitDto dto = contactLimitTestClient.create(createDto);
        assertCreateDtoMatchesDto(createDto, dto);
        assertCreateDtoMatchesDto(createDto, contactLimitTestClient.getById(dto.getId()));
    }

    @Test
    void patchTest() {
        ContactLimitDto dto = contactLimitGenerator.createAndSave(1);
        ContactLimitPatchDto patchDto = ContactLimitPatchDto.builder().id(dto.getId()).build();

        patchAndAssertBadRequest(patchDto.toBuilder().currency(Optional.empty()).build());
        patchAndAssertOk(dto, CurrencyCode.USD, c -> dto.setCurrency(CurrencyDto.of(c)), p -> p::setCurrency);

        patchAndAssertBadRequest(patchDto.toBuilder().threshold(Optional.empty()).build());
        patchAndAssertOk(dto, dto.getThreshold() + 1, dto::setThreshold, p -> p::setThreshold);

        patchAndAssertBadRequest(patchDto.toBuilder().targetPercent(Optional.empty()).build());
        patchAndAssertOk(dto, dto.getTargetPercent() + 1, dto::setTargetPercent, p -> p::setTargetPercent);

        patchAndAssertBadRequest(patchDto.toBuilder().maxTargetPercent(Optional.empty()).build());
        patchAndAssertOk(dto, dto.getMaxTargetPercent() + 1, dto::setMaxTargetPercent, p -> p::setMaxTargetPercent);

        patchAndAssertBadRequest(patchDto.toBuilder().floorLimit(Optional.empty()).build());
        patchAndAssertOk(dto, dto.getFloorLimit() + 1, dto::setFloorLimit, p -> p::setFloorLimit);

        patchAndAssertBadRequest(patchDto.toBuilder().pinBypassAllowed(Optional.empty()).build());
        patchAndAssertOk(dto, !dto.getPinBypassAllowed(), dto::setPinBypassAllowed, p -> p::setPinBypassAllowed);

        patchAndAssertBadRequest(patchDto.toBuilder().pinLimit(Optional.empty()).build());
        patchAndAssertOk(dto, dto.getPinLimit() + 1, dto::setPinLimit, p -> p::setPinLimit);

        assertDoesNotThrow(() -> patchDto.toBuilder().requestLast4DigitsPAN(Collections.emptySet()).build());
        assertDoesNotThrow(() -> patchDto.toBuilder().requestLast4DigitsPAN(Set.of(AcquirerOperationType.SALE)).build());
    }

    @Test
    void deleteByIdTest() {
        ContactLimitDto dto = contactLimitGenerator.createAndSave(1);
        assertNotNull(contactLimitTestClient.getById(dto.getId()).getId());
        contactLimitTestClient.deleteById(dto.getId());
        contactLimitTestClient.assertNotExists(dto.getId());
    }

    @Test
    void archiveAndRestoreByIdTest() {
        ContactLimitDto dto = contactLimitGenerator.createAndSave(1);
        assertFalse(contactLimitTestClient.getById(dto.getId()).getArchived());

        contactLimitTestClient.archiveById(dto.getId());
        assertTrue(contactLimitTestClient.getById(dto.getId()).getArchived());
        securityManager.runAsSystemUser(() -> assertNotNull(contactLimitService.getByIdOrElseThrow(dto.getId()).getLastArchivedAt()));

        contactLimitTestClient.restoreById(dto.getId());
        assertFalse(contactLimitTestClient.getById(dto.getId()).getArchived());
        securityManager.runAsSystemUser(() -> assertNotNull(contactLimitService.getByIdOrElseThrow(dto.getId()).getLastRestoredAt()));
    }

    private void createAndAssertBadRequest(ContactLimitCreateDto createDto) {
        assertThrowsResponseStatusException(() -> contactLimitTestClient.create(createDto), HttpStatus.BAD_REQUEST);
    }

    private void assertCreateDtoMatchesDto(ContactLimitCreateDto createDto, ContactLimitDto dto) {
        assertEquals(createDto.getCurrency().getNumeric(), dto.getCurrency().getNumericCode());
        assertEquals(createDto.getThreshold(), dto.getThreshold());
        assertEquals(createDto.getTargetPercent(), dto.getTargetPercent());
        assertEquals(createDto.getMaxTargetPercent(), dto.getMaxTargetPercent());
        assertEquals(createDto.getFloorLimit(), dto.getFloorLimit());
        assertEquals(createDto.getPinBypassAllowed(), dto.getPinBypassAllowed());
        assertEquals(createDto.getPinLimit(), dto.getPinLimit());
        assertEquals(createDto.getRequestLast4DigitsPAN(), dto.getRequestLast4DigitsPAN());
    }

    private <T> void patchAndAssertOk(ContactLimitDto dto,
                                      T newValue,
                                      Consumer<T> dtoSetter,
                                      Function<ContactLimitPatchDto, Consumer<Optional<T>>> patchDtoSetterSupplier) {
        assertEquals(dto, contactLimitTestClient.getById(dto.getId()));
        dtoSetter.accept(newValue);
        assertNotEquals(dto, contactLimitTestClient.getById(dto.getId()));
        ContactLimitPatchDto patchDto = ContactLimitPatchDto.builder().id(dto.getId()).build();
        patchDtoSetterSupplier.apply(patchDto).accept(Optional.of(newValue));
        assertEquals(dto, contactLimitTestClient.patch(patchDto));
        assertEquals(dto, contactLimitTestClient.getById(dto.getId()));
    }

    private void patchAndAssertBadRequest(ContactLimitPatchDto patchDto) {
        assertThrowsResponseStatusException(() -> contactLimitTestClient.patch(patchDto), HttpStatus.BAD_REQUEST);
    }
}
