package com.tms.profilemanager.app.integration;

import com.tms.profilemanager.app.entity.BaseEntity;
import com.tms.profilemanager.app.entity.PaymentSystem;
import com.tms.profilemanager.app.service.PaymentSystemService;
import com.tms.profilemanager.dto.BaseDto;
import com.tms.profilemanager.dto.PaymentSystemDto;
import com.tms.profilemanager.dto.create.PaymentSystemCreateDto;
import com.tms.profilemanager.dto.filter.PaymentSystemFilterDto;
import com.tms.profilemanager.dto.patch.PaymentSystemPatchDto;
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
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.toList;
import static org.apache.commons.lang3.StringUtils.repeat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.clearInvocations;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

class PaymentSystemIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    PaymentSystemService paymentSystemService;

    Long preparedContactLimitId;
    Long preparedContactlessLimitId;
    Long preparedGeneralLimitId;

    @BeforeEach
    public void beforeEach() {
        databaseCleaner.clearDatabaseAndExecuteJavaMigrations();
        preparedContactLimitId = contactLimitGenerator.createAndSave(1).getId();
        preparedContactlessLimitId = contactlessLimitGenerator.createAndSave(1, List.of()).getId();
        preparedGeneralLimitId = generalLimitGenerator.createAndSave(1, preparedContactLimitId, preparedContactlessLimitId).getId();
        drlGenerator.createAndSave(1, preparedContactlessLimitId);
        requestSender.setCurrentUser(ADMIN);
    }

    @Test
    void getByIdTest() {
        PaymentSystemDto dto = createAndSavePaymentSystem(1);
        assertEquals(dto, paymentSystemTestClient.getById(dto.getId()));
    }

    @Test
    void cacheByIdTest() {
        clearInvocations(paymentSystemRepository);

        // get from database because cache is empty
        assertTrue(paymentSystemService.getByIdUsingCacheWithoutAccessControl(1L).isEmpty());
        verify(paymentSystemRepository, times(1)).findById(any(Long.class));

        // get from database because result == null (null values are not cached)
        assertTrue(paymentSystemService.getByIdUsingCacheWithoutAccessControl(1L).isEmpty());
        verify(paymentSystemRepository, times(2)).findById(any(Long.class));

        // create new payment system
        Long paymentSystemId = createAndSavePaymentSystem(1).getId();
        PaymentSystem createdPaymentSystem = paymentSystemRepository.findById(paymentSystemId).orElseThrow();

        clearInvocations(paymentSystemRepository);

        // get from database because cache is empty
        assertEquals(createdPaymentSystem, paymentSystemService.getByIdUsingCacheWithoutAccessControl(paymentSystemId).orElseThrow());
        verify(paymentSystemRepository, times(1)).findById(any(Long.class));

        // get from cache and get the same result
        assertEquals(createdPaymentSystem, paymentSystemService.getByIdUsingCacheWithoutAccessControl(paymentSystemId).orElseThrow());
        verify(paymentSystemRepository, times(1)).findById(any(Long.class));

        // patch payment system
        paymentSystemTestClient.patch(PaymentSystemPatchDto.builder()
                .id(createdPaymentSystem.getId())
                .limits(createdPaymentSystem.getLimits().stream().map(BaseEntity::getId).collect(Collectors.toSet()))
                .name(Optional.of("New name"))
                .build());
        PaymentSystem patchedPaymentSystem = paymentSystemRepository.findById(paymentSystemId).orElseThrow();
        assertNotEquals(createdPaymentSystem, patchedPaymentSystem);

        clearInvocations(paymentSystemRepository);

        // get from database because cache is empty
        assertEquals(patchedPaymentSystem, paymentSystemService.getByIdUsingCacheWithoutAccessControl(paymentSystemId).orElseThrow());
        verify(paymentSystemRepository, times(1)).findById(any(Long.class));

        // get from cache and get the same result
        assertEquals(patchedPaymentSystem, paymentSystemService.getByIdUsingCacheWithoutAccessControl(paymentSystemId).orElseThrow());
        verify(paymentSystemRepository, times(1)).findById(any(Long.class));

        // delete payment system
        paymentSystemTestClient.deleteById(paymentSystemId);

        clearInvocations(paymentSystemRepository);

        // get from database because cache is empty
        assertTrue(paymentSystemService.getByIdUsingCacheWithoutAccessControl(paymentSystemId).isEmpty());
        verify(paymentSystemRepository, times(1)).findById(any(Long.class));
    }

    @Test
    void searchTest() {
        IntStream.of(1, 2, 3).forEach(this::createAndSavePaymentSystem);
        PaymentSystemFilterDto filterDto = new PaymentSystemFilterDto();
        List<Long> ids = paymentSystemTestClient.search(filterDto).stream().map(BaseDto::getId).collect(toList());
        assertEquals(3, ids.size());
        filterDto.setIds(new HashSet<>(ids.subList(0, 2)));
        assertEquals(2, paymentSystemTestClient.search(filterDto).getTotalElements());

        Long acquirer1Id = acquirerGenerator.createAndSave(1, Set.of(ids.get(0))).getId();
        acquirerGenerator.createAndSave(2, Set.of(ids.get(1)));

        filterDto = new PaymentSystemFilterDto();
        filterDto.setAcquirerIds(Set.of(acquirer1Id));
        List<PaymentSystemDto> paymentSystems = paymentSystemTestClient.search(filterDto).getContent();
        assertEquals(1, paymentSystems.size());
        assertEquals(ids.get(0), paymentSystems.get(0).getId());
    }

    @Test
    void createTest() {
        PaymentSystemDto existingPaymentSystemDto = createAndSavePaymentSystem(1);
        PaymentSystemCreateDto createDto = paymentSystemGenerator.buildCreateDto(2, preparedGeneralLimitId);
        Long preparedGeneralLimitId1 = generalLimitGenerator.createAndSave(2, preparedContactLimitId, preparedContactlessLimitId).getId();

        createAndAssertBadRequest(createDto.toBuilder().name(null).build());
        createAndAssertBadRequest(createDto.toBuilder().name("").build());
        createAndAssertBadRequest(createDto.toBuilder().name(existingPaymentSystemDto.getName()).build());
        createAndAssertBadRequest(createDto.toBuilder().name(repeat("*", 37)).build());

        createAndAssertBadRequest(createDto.toBuilder().avn(null).build());
        createAndAssertBadRequest(createDto.toBuilder().avn("").build());
        createAndAssertBadRequest(createDto.toBuilder().avn(repeat("*", 5)).build());

        createAndAssertBadRequest(createDto.toBuilder().limits(Collections.emptySet()).build());
        createAndAssertBadRequest(createDto.toBuilder().limits(Set.of(preparedGeneralLimitId, preparedGeneralLimitId1)).build());

        PaymentSystemDto dto = paymentSystemTestClient.create(createDto);
        assertCreateDtoMatchesDto(createDto, dto);
        assertCreateDtoMatchesDto(createDto, paymentSystemTestClient.getById(dto.getId()));
    }

    @Test
    void patchTest() {
        PaymentSystemDto existingPaymentSystemDto = createAndSavePaymentSystem(1);
        PaymentSystemDto dto = createAndSavePaymentSystem(2);
        PaymentSystemPatchDto patchDto = PaymentSystemPatchDto.builder().id(dto.getId()).build();
        Long preparedGeneralLimitId1 = generalLimitGenerator.createAndSave(3, preparedContactLimitId, preparedContactlessLimitId).getId();

        patchAndAssertBadRequest(patchDto.toBuilder().name(Optional.empty()).build());
        patchAndAssertBadRequest(patchDto.toBuilder().name(Optional.of("")).build());
        patchAndAssertBadRequest(patchDto.toBuilder().name(Optional.of(repeat("*", 37))).build());
        patchAndAssertBadRequest(patchDto.toBuilder().name(Optional.of(existingPaymentSystemDto.getName())).build());

        patchAndAssertBadRequest(patchDto.toBuilder().avn(Optional.empty()).build());
        patchAndAssertBadRequest(patchDto.toBuilder().avn(Optional.of("")).build());
        patchAndAssertBadRequest(patchDto.toBuilder().avn(Optional.of(repeat("*", 5))).build());

        patchAndAssertBadRequest(patchDto.toBuilder().contactlessAvn(Optional.empty()).build());
        patchAndAssertBadRequest(patchDto.toBuilder().contactlessAvn(Optional.of("")).build());
        patchAndAssertBadRequest(patchDto.toBuilder().contactlessAvn(Optional.of(repeat("*", 5))).build());

        patchAndAssertBadRequest(patchDto.toBuilder().limits(Collections.emptySet()).build());
        patchAndAssertBadRequest(patchDto.toBuilder().limits(Set.of(preparedGeneralLimitId, preparedGeneralLimitId1)).build());

        assertDoesNotThrow(() -> paymentSystemTestClient.patch(patchDto.toBuilder()
                .name(Optional.of("abc"))
                .avn(Optional.of("def"))
                .contactlessAvn(Optional.of("ghi"))
                .limits(Set.of(preparedGeneralLimitId))
                .build()));
        PaymentSystemDto afterPatch = paymentSystemTestClient.getById(patchDto.getId());
        assertEquals("abc", afterPatch.getName());
        assertEquals("def", afterPatch.getAvn());
        assertEquals("ghi", afterPatch.getContactlessAvn());
        assertEquals(1, afterPatch.getLimits().size());
        assertEquals(preparedGeneralLimitId, afterPatch.getLimits().stream().findFirst().get().getId());
    }

    @Test
    void deleteByIdTest() {
        PaymentSystemDto dto = createAndSavePaymentSystem(1);
        assertNotNull(paymentSystemTestClient.getById(dto.getId()).getId());
        paymentSystemTestClient.deleteById(dto.getId());
        paymentSystemTestClient.assertNotExists(dto.getId());
    }

    @Test
    void archiveAndRestoreByIdTest() {
        PaymentSystemDto dto = createAndSavePaymentSystem(1);
        assertFalse(paymentSystemTestClient.getById(dto.getId()).getArchived());

        paymentSystemTestClient.archiveById(dto.getId());
        assertTrue(paymentSystemTestClient.getById(dto.getId()).getArchived());
        securityManager.runAsSystemUser(() -> assertNotNull(paymentSystemService.getByIdOrElseThrow(dto.getId()).getLastArchivedAt()));

        paymentSystemTestClient.restoreById(dto.getId());
        assertFalse(paymentSystemTestClient.getById(dto.getId()).getArchived());
        securityManager.runAsSystemUser(() -> assertNotNull(paymentSystemService.getByIdOrElseThrow(dto.getId()).getLastRestoredAt()));
    }

    private void createAndAssertBadRequest(PaymentSystemCreateDto createDto) {
        assertThrowsResponseStatusException(() -> paymentSystemTestClient.create(createDto), HttpStatus.BAD_REQUEST);
    }

    private void assertCreateDtoMatchesDto(PaymentSystemCreateDto createDto, PaymentSystemDto dto) {
        assertEquals(createDto.getName(), dto.getName());
        assertEquals(createDto.getAvn(), dto.getAvn());
        assertEquals(createDto.getLimits(), dto.getLimits().stream().map(BaseDto::getId).collect(Collectors.toSet()));
    }

    private <T> void patchAndAssertOk(PaymentSystemDto dto,
                                      T newValue,
                                      Consumer<T> dtoSetter,
                                      Function<PaymentSystemPatchDto, Consumer<Optional<T>>> patchDtoSetterSupplier) {
        assertEquals(dto, paymentSystemTestClient.getById(dto.getId()));
        dtoSetter.accept(newValue);
        assertNotEquals(dto, paymentSystemTestClient.getById(dto.getId()));
        PaymentSystemPatchDto patchDto = PaymentSystemPatchDto.builder().id(dto.getId()).build();
        patchDtoSetterSupplier.apply(patchDto).accept(Optional.of(newValue));
        assertEquals(dto, paymentSystemTestClient.patch(patchDto));
        assertEquals(dto, paymentSystemTestClient.getById(dto.getId()));
    }

    private void patchAndAssertBadRequest(PaymentSystemPatchDto patchDto) {
        assertThrowsResponseStatusException(() -> paymentSystemTestClient.patch(patchDto), HttpStatus.BAD_REQUEST);
    }

    private PaymentSystemDto createAndSavePaymentSystem(int number) {
        return paymentSystemGenerator.createAndSave(number, preparedGeneralLimitId);
    }
}
