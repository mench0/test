package com.tms.profilemanager.app.integration;

import com.tms.profilemanager.app.entity.ContactlessApp;
import com.tms.profilemanager.app.service.ContactlessAppService;
import com.tms.profilemanager.dto.BaseDto;
import com.tms.profilemanager.dto.ContactlessAppDto;
import com.tms.profilemanager.dto.create.ContactlessAppCreateDto;
import com.tms.profilemanager.dto.enums.TerminalType;
import com.tms.profilemanager.dto.filter.ContactlessAppFilterDto;
import com.tms.profilemanager.dto.patch.ContactlessAppPatchDto;
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

import static com.tms.profilemanager.dto.enums.TerminalType.ATTENDENT_11;
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

class ContactlessAppIntegrationTest extends AbstractIntegrationTest {

    @Autowired ContactlessAppService contactlessAppService;

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
        ContactlessAppDto dto = contactlessAppGenerator.createAndSave(1, preparedPaymentSystemId, ATTENDENT_11);
        assertEquals(dto, contactlessAppTestClient.getById(dto.getId()));
    }

    @Test
    void cacheByPaymentSystemIdTest() {
        clearInvocations(contactlessAppRepository);

        // get from database because cache is empty
        assertTrue(contactlessAppService.getByPaymentSystemIdUsingCacheWithoutAccessControl(1L).isEmpty());
        verify(contactlessAppRepository, times(1)).findByPaymentSystemId(any(Long.class));

        // get from cache
        assertTrue(contactlessAppService.getByPaymentSystemIdUsingCacheWithoutAccessControl(1L).isEmpty());
        verify(contactlessAppRepository, times(1)).findByPaymentSystemId(any(Long.class));

        // create new contactlessApp
        Long contactlessAppId = contactlessAppGenerator.createAndSave(1, preparedPaymentSystemId, ATTENDENT_11).getId();
        ContactlessApp createdContactlessApp = contactlessAppRepository.findById(contactlessAppId).orElseThrow();

        clearInvocations(contactlessAppRepository);

        // get from database because cache is empty
        assertEquals(createdContactlessApp, contactlessAppService.getByPaymentSystemIdUsingCacheWithoutAccessControl(preparedPaymentSystemId).get(0));
        verify(contactlessAppRepository, times(1)).findByPaymentSystemId(any(Long.class));

        // get from cache and get the same result
        assertEquals(createdContactlessApp, contactlessAppService.getByPaymentSystemIdUsingCacheWithoutAccessControl(preparedPaymentSystemId).get(0));
        verify(contactlessAppRepository, times(1)).findByPaymentSystemId(any(Long.class));

        // patch payment system
        contactlessAppTestClient.patch(ContactlessAppPatchDto.builder()
                .id(createdContactlessApp.getId())
                .aid(Optional.of("New aid"))
                .build());
        ContactlessApp patchedContactlessApp = contactlessAppRepository.findById(contactlessAppId).orElseThrow();
        assertNotEquals(createdContactlessApp, patchedContactlessApp);

        clearInvocations(contactlessAppRepository);

        // get from database because cache is empty
        assertEquals(patchedContactlessApp, contactlessAppService.getByPaymentSystemIdUsingCacheWithoutAccessControl(preparedPaymentSystemId).get(0));
        verify(contactlessAppRepository, times(1)).findByPaymentSystemId(any(Long.class));

        // get from cache and get the same result
        assertEquals(patchedContactlessApp, contactlessAppService.getByPaymentSystemIdUsingCacheWithoutAccessControl(preparedPaymentSystemId).get(0));
        verify(contactlessAppRepository, times(1)).findByPaymentSystemId(any(Long.class));

        // delete payment system
        contactlessAppTestClient.deleteById(contactlessAppId);

        clearInvocations(contactlessAppRepository);

        // get from database because cache is empty
        assertTrue(contactlessAppService.getByPaymentSystemIdUsingCacheWithoutAccessControl(preparedPaymentSystemId).isEmpty());
        verify(contactlessAppRepository, times(1)).findByPaymentSystemId(any(Long.class));
    }

    @Test
    void searchTest() {
        Long paymentSystem2Id = paymentSystemGenerator.createAndSave(2, preparedGeneralLimitId).getId();
        contactlessAppGenerator.createAndSave(1, preparedPaymentSystemId, ATTENDENT_11);
        contactlessAppGenerator.createAndSave(2, paymentSystem2Id, ATTENDENT_11);
        contactlessAppGenerator.createAndSave(3, paymentSystem2Id, ATTENDENT_11);

        ContactlessAppFilterDto filterDto = new ContactlessAppFilterDto();
        List<Long> ids = contactlessAppTestClient.search(filterDto).stream().map(BaseDto::getId).collect(toList());
        assertEquals(3, ids.size());
        filterDto.setIds(new HashSet<>(ids.subList(0, 2)));
        assertEquals(2, contactlessAppTestClient.search(filterDto).getTotalElements());

        filterDto.setIds(null);
        filterDto.setPaymentSystemIds(Set.of(paymentSystem2Id));
        assertEquals(2, contactlessAppTestClient.search(filterDto).getTotalElements());
    }

    @Test
    void createTest() {
        ContactlessAppCreateDto createDto = contactlessAppGenerator.buildCreateDto(1, preparedPaymentSystemId, ATTENDENT_11);

        createAndAssertBadRequest(createDto.toBuilder().aid(null).build());
        createAndAssertBadRequest(createDto.toBuilder().aid(repeat("*", 33)).build());

        createAndAssertBadRequest(createDto.toBuilder().kernelId(null).build());
        createAndAssertBadRequest(createDto.toBuilder().kernelId(repeat("*", 3)).build());

        createAndAssertBadRequest(createDto.toBuilder().kernelConfiguration(null).build());
        createAndAssertBadRequest(createDto.toBuilder().kernelConfiguration(repeat("*", 4)).build());

        createAndAssertBadRequest(createDto.toBuilder().terminalCaps(null).build());
        createAndAssertBadRequest(createDto.toBuilder().terminalCaps(repeat("*", 7)).build());

        createAndAssertBadRequest(createDto.toBuilder().terminalCapsCVMNotRequired(null).build());
        createAndAssertBadRequest(createDto.toBuilder().terminalCapsCVMNotRequired(repeat("*", 7)).build());

        createAndAssertBadRequest(createDto.toBuilder().terminalAdditionalCaps(null).build());
        createAndAssertBadRequest(createDto.toBuilder().terminalAdditionalCaps(repeat("*", 11)).build());

        createAndAssertBadRequest(createDto.toBuilder().tacDefault(null).build());
        createAndAssertBadRequest(createDto.toBuilder().tacDefault(repeat("*", 11)).build());

        createAndAssertBadRequest(createDto.toBuilder().tacOnline(null).build());
        createAndAssertBadRequest(createDto.toBuilder().tacOnline(repeat("*", 11)).build());

        createAndAssertBadRequest(createDto.toBuilder().tacDenial(null).build());
        createAndAssertBadRequest(createDto.toBuilder().tacDenial(repeat("*", 11)).build());

        createAndAssertBadRequest(createDto.toBuilder().paymentSystemId(null).build());
        createAndAssertBadRequest(createDto.toBuilder().paymentSystemId(-1L).build());

        assertDoesNotThrow(() -> createDto.toBuilder().terminalType(null).build());

        ContactlessAppDto dto = contactlessAppTestClient.create(createDto);
        assertCreateDtoMatchesDto(createDto, dto);
        assertCreateDtoMatchesDto(createDto, contactlessAppTestClient.getById(dto.getId()));
    }

    @Test
    void patchTest() {
        ContactlessAppDto dto = contactlessAppGenerator.createAndSave(1, preparedPaymentSystemId, ATTENDENT_11);
        ContactlessAppPatchDto patchDto = ContactlessAppPatchDto.builder().id(dto.getId()).build();

        patchAndAssertBadRequest(patchDto.toBuilder().aid(Optional.empty()).build());
        patchAndAssertBadRequest(patchDto.toBuilder().aid(Optional.of(repeat("*", 33))).build());
        patchAndAssertOk(dto, "newAid", dto::setAid, p -> p::setAid);

        patchAndAssertBadRequest(patchDto.toBuilder().kernelId(Optional.empty()).build());
        patchAndAssertBadRequest(patchDto.toBuilder().kernelId(Optional.of(repeat("*", 3))).build());
        patchAndAssertOk(dto, "a", dto::setKernelId, p -> p::setKernelId);

        patchAndAssertBadRequest(patchDto.toBuilder().kernelConfiguration(Optional.empty()).build());
        patchAndAssertBadRequest(patchDto.toBuilder().kernelConfiguration(Optional.of(repeat("*", 4))).build());
        patchAndAssertOk(dto, "a", dto::setKernelConfiguration, p -> p::setKernelConfiguration);

        patchAndAssertBadRequest(patchDto.toBuilder().terminalCaps(Optional.empty()).build());
        patchAndAssertBadRequest(patchDto.toBuilder().terminalCaps(Optional.of(repeat("*", 7))).build());
        patchAndAssertOk(dto, "a", dto::setTerminalCaps, p -> p::setTerminalCaps);

        patchAndAssertBadRequest(patchDto.toBuilder().terminalCapsCVMNotRequired(Optional.empty()).build());
        patchAndAssertBadRequest(patchDto.toBuilder().terminalCapsCVMNotRequired(Optional.of(repeat("*", 7))).build());
        patchAndAssertOk(dto, "a", dto::setTerminalCapsCVMNotRequired, p -> p::setTerminalCapsCVMNotRequired);

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

        patchAndAssertBadRequest(patchDto.toBuilder().paymentSystemId(Optional.empty()).build());
        patchAndAssertBadRequest(patchDto.toBuilder().paymentSystemId(Optional.of(-1L)).build());
        Long paymentSystemId = paymentSystemGenerator.createAndSave(2, preparedGeneralLimitId).getId();
        patchAndAssertOk(dto, paymentSystemId, dto::setPaymentSystemId, p -> p::setPaymentSystemId);

        patchAndAssertOk(dto, TerminalType.ATTENDENT_22, dto::setTerminalType, p -> p::setTerminalType);

        Set<String> newTags = new HashSet<>(Set.of("newTag"));
        patchAndAssertOk(dto, newTags, dto::setTags, p -> p::setTags);
    }

    @Test
    void deleteByIdTest() {
        ContactlessAppDto dto = contactlessAppGenerator.createAndSave(1, preparedPaymentSystemId, ATTENDENT_11);
        assertNotNull(contactlessAppTestClient.getById(dto.getId()).getId());
        contactlessAppTestClient.deleteById(dto.getId());
        contactlessAppTestClient.assertNotExists(dto.getId());
    }

    @Test
    void archiveAndRestoreByIdTest() {
        ContactlessAppDto dto = contactlessAppGenerator.createAndSave(1, preparedPaymentSystemId, ATTENDENT_11);
        assertFalse(contactlessAppTestClient.getById(dto.getId()).getArchived());

        contactlessAppTestClient.archiveById(dto.getId());
        assertTrue(contactlessAppTestClient.getById(dto.getId()).getArchived());
        securityManager.runAsSystemUser(() -> assertNotNull(contactlessAppService.getByIdOrElseThrow(dto.getId()).getLastArchivedAt()));

        contactlessAppTestClient.restoreById(dto.getId());
        assertFalse(contactlessAppTestClient.getById(dto.getId()).getArchived());
        securityManager.runAsSystemUser(() -> assertNotNull(contactlessAppService.getByIdOrElseThrow(dto.getId()).getLastRestoredAt()));
    }

    private void createAndAssertBadRequest(ContactlessAppCreateDto createDto) {
        assertThrowsResponseStatusException(() -> contactlessAppTestClient.create(createDto), HttpStatus.BAD_REQUEST);
    }

    private void assertCreateDtoMatchesDto(ContactlessAppCreateDto createDto, ContactlessAppDto dto) {
        assertEquals(createDto.getAid(), dto.getAid());
        assertEquals(createDto.getKernelId(), dto.getKernelId());
        assertEquals(createDto.getKernelConfiguration(), dto.getKernelConfiguration());
        assertEquals(createDto.getTerminalCaps(), dto.getTerminalCaps());
        assertEquals(createDto.getTerminalCapsCVMNotRequired(), dto.getTerminalCapsCVMNotRequired());
        assertEquals(createDto.getTerminalAdditionalCaps(), dto.getTerminalAdditionalCaps());
        assertEquals(createDto.getTacDefault(), dto.getTacDefault());
        assertEquals(createDto.getTacOnline(), dto.getTacOnline());
        assertEquals(createDto.getTacDenial(), dto.getTacDenial());
        assertEquals(createDto.getPaymentSystemId(), dto.getPaymentSystemId());
        assertEquals(createDto.getTags(), dto.getTags());
    }

    private <T> void patchAndAssertOk(ContactlessAppDto dto,
                                      T newValue,
                                      Consumer<T> dtoSetter,
                                      Function<ContactlessAppPatchDto, Consumer<Optional<T>>> patchDtoSetterSupplier) {
        assertEquals(dto, contactlessAppTestClient.getById(dto.getId()));
        dtoSetter.accept(newValue);
        assertNotEquals(dto, contactlessAppTestClient.getById(dto.getId()));
        ContactlessAppPatchDto patchDto = ContactlessAppPatchDto.builder().id(dto.getId()).build();
        patchDtoSetterSupplier.apply(patchDto).accept(Optional.of(newValue));
        assertEquals(dto, contactlessAppTestClient.patch(patchDto));
        assertEquals(dto, contactlessAppTestClient.getById(dto.getId()));
    }

    private void patchAndAssertBadRequest(ContactlessAppPatchDto patchDto) {
        assertThrowsResponseStatusException(() -> contactlessAppTestClient.patch(patchDto), HttpStatus.BAD_REQUEST);
    }
}
