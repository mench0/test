package com.tms.profilemanager.app.integration;

import com.tms.profilemanager.app.entity.EMVApp;
import com.tms.profilemanager.app.service.EMVAppService;
import com.tms.profilemanager.dto.BaseDto;
import com.tms.profilemanager.dto.EMVAppDto;
import com.tms.profilemanager.dto.create.EMVAppCreateDto;
import com.tms.profilemanager.dto.enums.TerminalType;
import com.tms.profilemanager.dto.filter.EMVAppFilterDto;
import com.tms.profilemanager.dto.patch.EMVAppPatchDto;
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

class EMVAppIntegrationTest extends AbstractIntegrationTest {

    @Autowired EMVAppService emvAppService;

    Long preparedPaymentSystemId;
    Long preparedContactLimitId;
    Long preparedContactlessLimitId;
    Long preparedGeneralLimitId;

    @BeforeEach
    public void beforeEach() {
        databaseCleaner.clearDatabaseAndExecuteJavaMigrations();
        preparedContactLimitId = contactLimitGenerator.createAndSave(1).getId();
        preparedContactlessLimitId = contactlessLimitGenerator.createAndSave(1, List.of()).getId();
        preparedGeneralLimitId = generalLimitGenerator.createAndSave(1, preparedContactLimitId, preparedContactlessLimitId).getId();
        preparedPaymentSystemId = paymentSystemGenerator.createAndSave(1, preparedGeneralLimitId).getId();
        requestSender.setCurrentUser(ADMIN);
    }

    @Test
    void getByIdTest() {
        EMVAppDto dto = emvAppGenerator.createAndSave(1, preparedPaymentSystemId, TerminalType.ATTENDENT_11);
        assertEquals(dto, emvAppTestClient.getById(dto.getId()));
    }

    @Test
    void cacheByPaymentSystemIdTest() {
        clearInvocations(emvAppRepository);

        // get from database because cache is empty
        assertTrue(emvAppService.getByPaymentSystemIdUsingCacheWithoutAccessControl(1L).isEmpty());
        verify(emvAppRepository, times(1)).findByPaymentSystemId(any(Long.class));

        // get from cache
        assertTrue(emvAppService.getByPaymentSystemIdUsingCacheWithoutAccessControl(1L).isEmpty());
        verify(emvAppRepository, times(1)).findByPaymentSystemId(any(Long.class));

        // create new emvApp
        Long emvAppId = emvAppGenerator.createAndSave(1, preparedPaymentSystemId, TerminalType.ATTENDENT_11).getId();
        EMVApp createdEmvApp = emvAppRepository.findById(emvAppId).orElseThrow();

        clearInvocations(emvAppRepository);

        // get from database because cache is empty
        assertEquals(createdEmvApp, emvAppService.getByPaymentSystemIdUsingCacheWithoutAccessControl(preparedPaymentSystemId).get(0));
        verify(emvAppRepository, times(1)).findByPaymentSystemId(any(Long.class));

        // get from cache and get the same result
        assertEquals(createdEmvApp, emvAppService.getByPaymentSystemIdUsingCacheWithoutAccessControl(preparedPaymentSystemId).get(0));
        verify(emvAppRepository, times(1)).findByPaymentSystemId(any(Long.class));

        // patch payment system
        emvAppTestClient.patch(EMVAppPatchDto.builder()
                .id(createdEmvApp.getId())
                .aid(Optional.of("New aid"))
                .build());
        EMVApp patchedEmvApp = emvAppRepository.findById(emvAppId).orElseThrow();
        assertNotEquals(createdEmvApp, patchedEmvApp);

        clearInvocations(emvAppRepository);

        // get from database because cache is empty
        assertEquals(patchedEmvApp, emvAppService.getByPaymentSystemIdUsingCacheWithoutAccessControl(preparedPaymentSystemId).get(0));
        verify(emvAppRepository, times(1)).findByPaymentSystemId(any(Long.class));

        // get from cache and get the same result
        assertEquals(patchedEmvApp, emvAppService.getByPaymentSystemIdUsingCacheWithoutAccessControl(preparedPaymentSystemId).get(0));
        verify(emvAppRepository, times(1)).findByPaymentSystemId(any(Long.class));

        // delete payment system
        emvAppTestClient.deleteById(emvAppId);

        clearInvocations(emvAppRepository);

        // get from database because cache is empty
        assertTrue(emvAppService.getByPaymentSystemIdUsingCacheWithoutAccessControl(preparedPaymentSystemId).isEmpty());
        verify(emvAppRepository, times(1)).findByPaymentSystemId(any(Long.class));
    }

    @Test
    void searchTest() {
        Long paymentSystem2Id = paymentSystemGenerator.createAndSave(2, preparedGeneralLimitId).getId();
        emvAppGenerator.createAndSave(1, preparedPaymentSystemId, TerminalType.ATTENDENT_11);
        emvAppGenerator.createAndSave(2, paymentSystem2Id, TerminalType.ATTENDENT_11);
        emvAppGenerator.createAndSave(3, paymentSystem2Id, TerminalType.ATTENDENT_11);

        EMVAppFilterDto filterDto = new EMVAppFilterDto();
        List<Long> ids = emvAppTestClient.search(filterDto).stream().map(BaseDto::getId).collect(toList());
        assertEquals(3, ids.size());
        filterDto.setIds(new HashSet<>(ids.subList(0, 2)));
        assertEquals(2, emvAppTestClient.search(filterDto).getTotalElements());

        filterDto.setIds(null);
        filterDto.setPaymentSystemIds(Set.of(paymentSystem2Id));
        assertEquals(2, emvAppTestClient.search(filterDto).getTotalElements());
    }

    @Test
    void createTest() {
        EMVAppCreateDto createDto = emvAppGenerator.buildCreateDto(1, preparedPaymentSystemId, TerminalType.ATTENDENT_11);

        createAndAssertBadRequest(createDto.toBuilder().aid(null).build());
        createAndAssertBadRequest(createDto.toBuilder().aid(repeat("*", 33)).build());

        createAndAssertBadRequest(createDto.toBuilder().terminalCaps(null).build());
        createAndAssertBadRequest(createDto.toBuilder().terminalCaps(repeat("*", 7)).build());

        createAndAssertBadRequest(createDto.toBuilder().terminalAdditionalCaps(null).build());
        createAndAssertBadRequest(createDto.toBuilder().terminalAdditionalCaps(repeat("*", 11)).build());

        createAndAssertBadRequest(createDto.toBuilder().tacDefault(null).build());
        createAndAssertBadRequest(createDto.toBuilder().tacDefault(repeat("*", 11)).build());

        createAndAssertBadRequest(createDto.toBuilder().tacOnline(null).build());
        createAndAssertBadRequest(createDto.toBuilder().tacOnline(repeat("*", 11)).build());

        createAndAssertBadRequest(createDto.toBuilder().tacDenial(null).build());
        createAndAssertBadRequest(createDto.toBuilder().tacDenial(repeat("*", 11)).build());

        createAndAssertBadRequest(createDto.toBuilder().tdol(null).build());
        createAndAssertBadRequest(createDto.toBuilder().tdol(repeat("*", 257)).build());

        createAndAssertBadRequest(createDto.toBuilder().ddol(null).build());
        createAndAssertBadRequest(createDto.toBuilder().ddol(repeat("*", 257)).build());

        createAndAssertBadRequest(createDto.toBuilder().paymentSystemId(null).build());
        createAndAssertBadRequest(createDto.toBuilder().paymentSystemId(-1L).build());

        assertDoesNotThrow(() -> createDto.toBuilder().terminalType(null).build());

        EMVAppDto dto = emvAppTestClient.create(createDto);
        assertCreateDtoMatchesDto(createDto, dto);
        assertCreateDtoMatchesDto(createDto, emvAppTestClient.getById(dto.getId()));
    }

    @Test
    void patchTest() {
        EMVAppDto dto = emvAppGenerator.createAndSave(1, preparedPaymentSystemId, TerminalType.ATTENDENT_11);
        EMVAppPatchDto patchDto = EMVAppPatchDto.builder().id(dto.getId()).build();

        patchAndAssertBadRequest(patchDto.toBuilder().aid(Optional.empty()).build());
        patchAndAssertBadRequest(patchDto.toBuilder().aid(Optional.of(repeat("*", 33))).build());
        patchAndAssertOk(dto, "a", dto::setAid, p -> p::setAid);

        patchAndAssertBadRequest(patchDto.toBuilder().terminalCaps(Optional.empty()).build());
        patchAndAssertBadRequest(patchDto.toBuilder().terminalCaps(Optional.of(repeat("*", 7))).build());
        patchAndAssertOk(dto, "a", dto::setTerminalCaps, p -> p::setTerminalCaps);

        patchAndAssertBadRequest(patchDto.toBuilder().terminalAdditionalCaps(Optional.empty()).build());
        patchAndAssertBadRequest(patchDto.toBuilder().terminalAdditionalCaps(Optional.of(repeat("*", 11))).build());
        patchAndAssertOk(dto, "a", dto::setTerminalAdditionalCaps, p -> p::setTerminalAdditionalCaps);

        patchAndAssertBadRequest(patchDto.toBuilder().tacDefault(Optional.empty()).build());
        patchAndAssertBadRequest(patchDto.toBuilder().tacDefault(Optional.of(repeat("*", 11))).build());
        patchAndAssertOk(dto, "a", dto::setTacDefault, p -> p::setTacDefault);

        patchAndAssertBadRequest(patchDto.toBuilder().tacOnline(Optional.empty()).build());
        patchAndAssertBadRequest(patchDto.toBuilder().tacOnline(Optional.of(repeat("*", 11))).build());
        patchAndAssertOk(dto, "a", dto::setTacOnline, p -> p::setTacOnline);

        patchAndAssertBadRequest(patchDto.toBuilder().tacDenial(Optional.empty()).build());
        patchAndAssertBadRequest(patchDto.toBuilder().tacDenial(Optional.of(repeat("*", 11))).build());
        patchAndAssertOk(dto, "a", dto::setTacDenial, p -> p::setTacDenial);

        patchAndAssertBadRequest(patchDto.toBuilder().tdol(Optional.empty()).build());
        patchAndAssertBadRequest(patchDto.toBuilder().tdol(Optional.of(repeat("*", 257))).build());
        patchAndAssertOk(dto, "a", dto::setTdol, p -> p::setTdol);

        patchAndAssertBadRequest(patchDto.toBuilder().ddol(Optional.empty()).build());
        patchAndAssertBadRequest(patchDto.toBuilder().ddol(Optional.of(repeat("*", 257))).build());
        patchAndAssertOk(dto, "a", dto::setDdol, p -> p::setDdol);

        patchAndAssertOk(dto, TerminalType.ATTENDENT_22, dto::setTerminalType, p -> p::setTerminalType);

        patchAndAssertBadRequest(patchDto.toBuilder().paymentSystemId(Optional.empty()).build());
        patchAndAssertBadRequest(patchDto.toBuilder().paymentSystemId(Optional.of(-1L)).build());
        Long paymentSystemId = paymentSystemGenerator.createAndSave(2, preparedGeneralLimitId).getId();
        patchAndAssertOk(dto, paymentSystemId, dto::setPaymentSystemId, p -> p::setPaymentSystemId);
    }

    @Test
    void deleteByIdTest() {
        EMVAppDto dto = emvAppGenerator.createAndSave(1, preparedPaymentSystemId, TerminalType.ATTENDENT_11);
        assertNotNull(emvAppTestClient.getById(dto.getId()).getId());
        emvAppTestClient.deleteById(dto.getId());
        emvAppTestClient.assertNotExists(dto.getId());
    }

    @Test
    void archiveAndRestoreByIdTest() {
        EMVAppDto dto = emvAppGenerator.createAndSave(1, preparedPaymentSystemId, TerminalType.ATTENDENT_11);
        assertFalse(emvAppTestClient.getById(dto.getId()).getArchived());

        emvAppTestClient.archiveById(dto.getId());
        assertTrue(emvAppTestClient.getById(dto.getId()).getArchived());
        securityManager.runAsSystemUser(() -> assertNotNull(emvAppService.getByIdOrElseThrow(dto.getId()).getLastArchivedAt()));

        emvAppTestClient.restoreById(dto.getId());
        assertFalse(emvAppTestClient.getById(dto.getId()).getArchived());
        securityManager.runAsSystemUser(() -> assertNotNull(emvAppService.getByIdOrElseThrow(dto.getId()).getLastRestoredAt()));
    }

    private void createAndAssertBadRequest(EMVAppCreateDto createDto) {
        assertThrowsResponseStatusException(() -> emvAppTestClient.create(createDto), HttpStatus.BAD_REQUEST);
    }

    private void assertCreateDtoMatchesDto(EMVAppCreateDto createDto, EMVAppDto dto) {
        assertEquals(createDto.getAid(), dto.getAid());
        assertEquals(createDto.getTerminalCaps(), dto.getTerminalCaps());
        assertEquals(createDto.getTerminalAdditionalCaps(), dto.getTerminalAdditionalCaps());
        assertEquals(createDto.getTacDefault(), dto.getTacDefault());
        assertEquals(createDto.getTacOnline(), dto.getTacOnline());
        assertEquals(createDto.getTacDenial(), dto.getTacDenial());
        assertEquals(createDto.getTdol(), dto.getTdol());
        assertEquals(createDto.getDdol(), dto.getDdol());
        assertEquals(createDto.getPaymentSystemId(), dto.getPaymentSystemId());
    }

    private <T> void patchAndAssertOk(EMVAppDto dto,
                                      T newValue,
                                      Consumer<T> dtoSetter,
                                      Function<EMVAppPatchDto, Consumer<Optional<T>>> patchDtoSetterSupplier) {
        assertEquals(dto, emvAppTestClient.getById(dto.getId()));
        dtoSetter.accept(newValue);
        assertNotEquals(dto, emvAppTestClient.getById(dto.getId()));
        EMVAppPatchDto patchDto = EMVAppPatchDto.builder().id(dto.getId()).build();
        patchDtoSetterSupplier.apply(patchDto).accept(Optional.of(newValue));
        assertEquals(dto, emvAppTestClient.patch(patchDto));
        assertEquals(dto, emvAppTestClient.getById(dto.getId()));
    }

    private void patchAndAssertBadRequest(EMVAppPatchDto patchDto) {
        assertThrowsResponseStatusException(() -> emvAppTestClient.patch(patchDto), HttpStatus.BAD_REQUEST);
    }
}
