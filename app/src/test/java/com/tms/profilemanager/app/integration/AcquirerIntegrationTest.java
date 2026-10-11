package com.tms.profilemanager.app.integration;

import com.neovisionaries.i18n.CurrencyCode;
import com.tms.profilemanager.app.entity.Acquirer;
import com.tms.profilemanager.app.service.AcquirerService;
import com.tms.profilemanager.dto.AcquirerDto;
import com.tms.profilemanager.dto.AcquirerDto.AcquirerPaymentSystemDto;
import com.tms.profilemanager.dto.BaseDto;
import com.tms.profilemanager.dto.CardRangeDto;
import com.tms.profilemanager.dto.ContactLimitDto;
import com.tms.profilemanager.dto.ContactlessLimitDto;
import com.tms.profilemanager.dto.CurrencyDto;
import com.tms.profilemanager.dto.GeneralLimitDto;
import com.tms.profilemanager.dto.create.AcquirerCreateDto;
import com.tms.profilemanager.dto.enums.AcquirerOperationType;
import com.tms.profilemanager.dto.enums.IsoProtocol;
import com.tms.profilemanager.dto.filter.AcquirerFilterDto;
import com.tms.profilemanager.dto.patch.AcquirerPatchDto;
import com.tms.profilemanager.dto.patch.AcquirerPatchDto.KeySettingsPatchDto;
import com.tms.profilemanager.dto.patch.AcquirerPatchDto.PaymentHostPatchDto;
import com.tms.profilemanager.dto.patch.BasePatchDto.SetPatch;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.IntStream;

import static com.tms.profilemanager.dto.enums.AcquirerKeyScheme.HUMO;
import static com.tms.profilemanager.dto.enums.AcquirerOperationType.SALE;
import static com.tms.profilemanager.dto.patch.BasePatchDto.CollectionPatchType.ADD;
import static com.tms.profilemanager.dto.patch.BasePatchDto.CollectionPatchType.REMOVE;
import static com.tms.profilemanager.dto.patch.BasePatchDto.CollectionPatchType.REPLACE;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;
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

class AcquirerIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    AcquirerService acquirerService;

    Long preparedPaymentSystemId1;
    Long preparedPaymentSystemId2;
    Long preparedContactLimitId;
    Long preparedContactlessLimitId;
    Long preparedGeneralLimitId;

    @BeforeEach
    public void beforeEach() {
        databaseCleaner.clearDatabaseAndExecuteJavaMigrations();
        preparedContactLimitId = contactLimitGenerator.createAndSave(1).getId();
        preparedContactlessLimitId = contactlessLimitGenerator.createAndSave(1, List.of()).getId();
        preparedGeneralLimitId = generalLimitGenerator.createAndSave(1, preparedContactLimitId, preparedContactlessLimitId).getId();
        preparedPaymentSystemId1 = paymentSystemGenerator.createAndSave(1, preparedGeneralLimitId).getId();
        preparedPaymentSystemId2 = paymentSystemGenerator.createAndSave(2, preparedGeneralLimitId).getId();
        requestSender.setCurrentUser(ADMIN);
    }

    @Test
    void getByIdTest() {
        AcquirerDto dto = acquirerGenerator.createAndSave(1, Set.of(preparedPaymentSystemId1));
        assertEquals(dto, acquirerTestClient.getById(dto.getId()));
    }

    @Test
    void cacheByIdTest() {
        clearInvocations(acquirerRepository);

        // get from database because cache is empty
        assertTrue(acquirerService.getByIdUsingCacheWithoutAccessControl(1L).isEmpty());
        verify(acquirerRepository, times(1)).getByIdAndArchivedFalse(any(Long.class));

        // get from database because result == null (null values are not cached)
        assertTrue(acquirerService.getByIdUsingCacheWithoutAccessControl(1L).isEmpty());
        verify(acquirerRepository, times(2)).getByIdAndArchivedFalse(any(Long.class));

        // create new acquirer
        Long acquirerId = acquirerGenerator.createAndSave(1, Set.of(preparedPaymentSystemId1)).getId();
        Acquirer createdAcquirer = acquirerRepository.getByIdAndArchivedFalse(acquirerId).orElseThrow();

        clearInvocations(acquirerRepository);

        // get from database because cache is empty
        assertEquals(createdAcquirer, acquirerService.getByIdUsingCacheWithoutAccessControl(acquirerId).orElseThrow());
        verify(acquirerRepository, times(1)).getByIdAndArchivedFalse(any(Long.class));

        // get from cache and get the same result
        assertEquals(createdAcquirer, acquirerService.getByIdUsingCacheWithoutAccessControl(acquirerId).orElseThrow());
        verify(acquirerRepository, times(1)).getByIdAndArchivedFalse(any(Long.class));

        // patch acquirer
        acquirerTestClient.patch(AcquirerPatchDto.builder()
                .id(createdAcquirer.getId())
                .name(Optional.of("New name"))
                .build());
        Acquirer patchedAcquirer = acquirerRepository.getByIdAndArchivedFalse(acquirerId).orElseThrow();
        assertNotEquals(createdAcquirer, patchedAcquirer);

        clearInvocations(acquirerRepository);

        // get from database because cache is empty
        assertEquals(patchedAcquirer, acquirerService.getByIdUsingCacheWithoutAccessControl(acquirerId).orElseThrow());
        verify(acquirerRepository, times(1)).getByIdAndArchivedFalse(any(Long.class));

        // get from cache and get the same result
        assertEquals(patchedAcquirer, acquirerService.getByIdUsingCacheWithoutAccessControl(acquirerId).orElseThrow());
        verify(acquirerRepository, times(1)).getByIdAndArchivedFalse(any(Long.class));

        // delete acquirer
        acquirerTestClient.deleteById(acquirerId);

        clearInvocations(acquirerRepository);

        // get from database because cache is empty
        assertTrue(acquirerService.getByIdUsingCacheWithoutAccessControl(acquirerId).isEmpty());
        verify(acquirerRepository, times(1)).getByIdAndArchivedFalse(any(Long.class));
    }

    @Test
    void searchTest() {
        IntStream.range(1, 4).forEach(it -> acquirerGenerator.createAndSave(it, Set.of(preparedPaymentSystemId1)));

        AcquirerFilterDto filterDto = new AcquirerFilterDto();
        List<Long> ids = acquirerTestClient.search(filterDto).stream().map(BaseDto::getId).collect(toList());
        assertEquals(3, ids.size());
        filterDto.setIds(new HashSet<>(ids.subList(0, 2)));
        assertEquals(2, acquirerTestClient.search(filterDto).getTotalElements());
    }

    @Test
    void createTest() {
        AcquirerDto existingAcquirerDto = acquirerGenerator.createAndSave(1, Set.of(preparedPaymentSystemId1));
        AcquirerCreateDto createDto = acquirerGenerator.buildCreateDto(2, Set.of(preparedPaymentSystemId1));

        createAndAssertBadRequest(createDto.toBuilder().name(null).build());
        createAndAssertBadRequest(createDto.toBuilder().name("").build());
        createAndAssertBadRequest(createDto.toBuilder().name(repeat("*", 256)).build());
        createAndAssertBadRequest(createDto.toBuilder().name(existingAcquirerDto.getName()).build());

        createAndAssertBadRequest(createDto.toBuilder().availableOperations(null).build());
        createAndAssertBadRequest(createDto.toBuilder().availableOperations(List.of()).build());

        createAndAssertBadRequest(createDto.toBuilder().availableCurrencies(null).build());
        createAndAssertBadRequest(createDto.toBuilder().availableCurrencies(Set.of()).build());

        createAndAssertBadRequest(createDto.toBuilder().usingMacToSignRequests(null).build());

        createAndAssertBadRequest(createDto.toBuilder().isoProtocol(null).build());

        createAndAssertBadRequest(createDto.toBuilder().paymentSystemsIds(null).build());
        createAndAssertBadRequest(createDto.toBuilder().paymentSystemsIds(Set.of()).build());
        createAndAssertBadRequest(createDto.toBuilder().paymentSystemsIds(Set.of(-1L)).build());

        createAndAssertBadRequest(createDto.toBuilder().keySettings(null).build());
        createAndAssertBadRequest(createDto.toBuilder().keySettings(createDto.getKeySettings().toBuilder().pinMasterKeyIndex(null).build()).build());
        createAndAssertBadRequest(createDto.toBuilder().keySettings(createDto.getKeySettings().toBuilder().pinWorkKeyIndex(null).build()).build());
        createAndAssertBadRequest(createDto.toBuilder().keySettings(createDto.getKeySettings().toBuilder().keyScheme(null).build()).build());
        createAndAssertBadRequest(createDto.toBuilder().paymentHost(createDto.getPaymentHost().toBuilder().host(null).build()).build());
        createAndAssertBadRequest(createDto.toBuilder().paymentHost(createDto.getPaymentHost().toBuilder().host("").build()).build());
        createAndAssertBadRequest(createDto.toBuilder().paymentHost(createDto.getPaymentHost().toBuilder().host("12345").build()).build());
        createAndAssertBadRequest(createDto.toBuilder().paymentHost(createDto.getPaymentHost().toBuilder().port(null).build()).build());
        createAndAssertBadRequest(createDto.toBuilder().paymentHost(createDto.getPaymentHost().toBuilder().useSsl(null).build()).build());

        createAndAssertBadRequest(createDto.toBuilder().rklUrl(createDto.getRklUrl().toBuilder().rklEnable(null).build()).build());
        createAndAssertBadRequest(createDto.toBuilder().rklUrl(createDto.getRklUrl().toBuilder().rklHost(null).build()).build());
        createAndAssertBadRequest(createDto.toBuilder().rklUrl(createDto.getRklUrl().toBuilder().rklHost("").build()).build());
        createAndAssertBadRequest(createDto.toBuilder().rklUrl(createDto.getRklUrl().toBuilder().rklHost("1233.123.123.123").build()).build());
        createAndAssertBadRequest(createDto.toBuilder().rklUrl(createDto.getRklUrl().toBuilder().rklHost("abcdфв.com").build()).build());
        createAndAssertBadRequest(createDto.toBuilder().rklUrl(createDto.getRklUrl().toBuilder().rklHost("22123").build()).build());
        createAndAssertBadRequest(createDto.toBuilder().rklUrl(createDto.getRklUrl().toBuilder().rklHost("1.1.1.1:8080").build()).build());
        createAndAssertBadRequest(createDto.toBuilder().rklUrl(createDto.getRklUrl().toBuilder().rklPort(123133).build()).build());
        createAndAssertBadRequest(createDto.toBuilder().rklUrl(createDto.getRklUrl().toBuilder().rklUseSsl(null).build()).build());


        assertDoesNotThrow(() -> createDto.toBuilder().cashbackLimit(null).build());
        assertDoesNotThrow(() -> createDto.toBuilder().cashbackLimit(1L).build());

        AcquirerDto dto = acquirerTestClient.create(createDto);
        assertCreateDtoMatchesDto(createDto, dto);
        assertCreateDtoMatchesDto(createDto, acquirerTestClient.getById(dto.getId()));
    }

    @Test
    void patchTest() {
        AcquirerDto existingAcquirerDto = acquirerGenerator.createAndSave(1, Set.of(preparedPaymentSystemId1));
        AcquirerDto dto = acquirerGenerator.createAndSave(2, Set.of(preparedPaymentSystemId1));
        AcquirerPatchDto patchDto = AcquirerPatchDto.builder().id(dto.getId()).build();

        patchAndAssertBadRequest(patchDto.toBuilder().name(Optional.empty()).build());
        patchAndAssertBadRequest(patchDto.toBuilder().name(Optional.of("")).build());
        patchAndAssertBadRequest(patchDto.toBuilder().name(Optional.of(repeat("*", 256))).build());
        patchAndAssertBadRequest(patchDto.toBuilder().name(Optional.of(existingAcquirerDto.getName())).build());
        assertEquals(dto, acquirerTestClient.patch(patchDto.toBuilder().name(Optional.of(dto.getName())).build()));
        patchAndAssertOk(dto, StringUtils.repeat("a", 10), dto::setName, p -> p::setName);

        patchAvailableOperationsTest(patchDto, dto);
        patchAvailableCurrenciesTest(patchDto, dto);

        patchAndAssertBadRequest(patchDto.toBuilder().usingMacToSignRequests(Optional.empty()).build());
        patchAndAssertOk(dto, !dto.getUsingMacToSignRequests(), dto::setUsingMacToSignRequests, p -> p::setUsingMacToSignRequests);

        patchAndAssertBadRequest(patchDto.toBuilder().isoProtocol(Optional.empty()).build());
        patchAndAssertOk(dto, IsoProtocol.SMART_VISTA_UZCARD, v -> dto.setIsoProtocol(IsoProtocol.Dto.of(v)), p -> p::setIsoProtocol);

        patchPaymentSystemsTest(patchDto, dto);

        patchAndAssertBadRequest(patchDto.toBuilder().keySettings(KeySettingsPatchDto.builder().pinMasterKeyIndex(Optional.empty()).build()).build());
        patchAndAssertOk(dto, 100, v -> dto.getKeySettings().setPinMasterKeyIndex(v), p -> v -> p.setKeySettings(KeySettingsPatchDto.builder().pinMasterKeyIndex(v).build()));
        patchAndAssertBadRequest(patchDto.toBuilder().keySettings(KeySettingsPatchDto.builder().pinWorkKeyIndex(Optional.empty()).build()).build());
        patchAndAssertBadRequest(patchDto.toBuilder().keySettings(KeySettingsPatchDto.builder().keyScheme(Optional.empty()).build()).build());

        patchAndAssertOk(dto, 110, v -> dto.getKeySettings().setPinWorkKeyIndex(v), p -> v -> p.setKeySettings(KeySettingsPatchDto.builder().pinWorkKeyIndex(v).build()));
        patchAndAssertOk(dto, 120, v -> dto.getKeySettings().setMacMasterKeyIndex(v), p -> v -> p.setKeySettings(KeySettingsPatchDto.builder().macMasterKeyIndex(v).build()));
        patchAndAssertOk(dto, 130, v -> dto.getKeySettings().setMacWorkingKeyIndex(v), p -> v -> p.setKeySettings(KeySettingsPatchDto.builder().macWorkingKeyIndex(v).build()));
        patchAndAssertOk(dto, HUMO, v -> dto.getKeySettings().setKeyScheme(v), p -> v -> p.setKeySettings(KeySettingsPatchDto.builder().keyScheme(v).build()));

        patchAndAssertBadRequest(patchDto.toBuilder().paymentHost(PaymentHostPatchDto.builder().host(Optional.empty()).build()).build());
        patchAndAssertBadRequest(patchDto.toBuilder().paymentHost(PaymentHostPatchDto.builder().host(Optional.of("12345")).build()).build());
        patchAndAssertOk(dto, "10.10.10.10", v -> dto.getPaymentHost().setHost(v), p -> v -> p.setPaymentHost(PaymentHostPatchDto.builder().host(v).build()));
        patchAndAssertBadRequest(patchDto.toBuilder().paymentHost(PaymentHostPatchDto.builder().port(Optional.empty()).build()).build());
        patchAndAssertOk(dto, 1000, v -> dto.getPaymentHost().setPort(v), p -> v -> p.setPaymentHost(PaymentHostPatchDto.builder().port(v).build()));
        patchAndAssertBadRequest(patchDto.toBuilder().paymentHost(PaymentHostPatchDto.builder().useSsl(Optional.empty()).build()).build());
        patchAndAssertOk(dto, !dto.getPaymentHost().getUseSsl(), v -> dto.getPaymentHost().setUseSsl(v), p -> v -> p.setPaymentHost(PaymentHostPatchDto.builder().useSsl(v).build()));

        patchAndAssertBadRequest(patchDto.toBuilder().rklUrl(AcquirerPatchDto.RklUrlPatchDto.builder().rklEnable(Optional.empty()).build()).build());
        patchAndAssertBadRequest(patchDto.toBuilder().rklUrl(AcquirerPatchDto.RklUrlPatchDto.builder().rklHost(Optional.of("12345")).build()).build());
        patchAndAssertOk(dto, "11.11.11.11", v -> dto.getRklUrl().setRklHost(v), p -> v -> p.setRklUrl(AcquirerPatchDto.RklUrlPatchDto.builder().rklHost(v).build()));
        patchAndAssertBadRequest(patchDto.toBuilder().rklUrl(AcquirerPatchDto.RklUrlPatchDto.builder().rklHost(Optional.of("2.2.2.2:8080")).build()).build());
        patchAndAssertBadRequest(patchDto.toBuilder().rklUrl(AcquirerPatchDto.RklUrlPatchDto.builder().rklHost(Optional.of("domain:8080.com")).build()).build());
        patchAndAssertBadRequest(patchDto.toBuilder().rklUrl(AcquirerPatchDto.RklUrlPatchDto.builder().rklPort(Optional.of(123456)).build()).build());
        patchAndAssertOk(dto, 12345, v -> dto.getRklUrl().setRklPort(v), p -> v -> p.setRklUrl(AcquirerPatchDto.RklUrlPatchDto.builder().rklPort(v).build()));
        patchAndAssertBadRequest(patchDto.toBuilder().rklUrl(AcquirerPatchDto.RklUrlPatchDto.builder().rklUseSsl(Optional.empty()).build()).build());
        patchAndAssertOk(dto, !dto.getRklUrl().getRklUseSsl(), v -> dto.getRklUrl().setRklUseSsl(v), p -> v -> p.setRklUrl(AcquirerPatchDto.RklUrlPatchDto.builder().rklUseSsl(v).build()));
        patchAndAssertOk(dto, !dto.getRklUrl().getRklEnable(), v -> dto.getRklUrl().setRklEnable(v), p -> v -> p.setRklUrl(AcquirerPatchDto.RklUrlPatchDto.builder().rklEnable(v).build()));

        patchAndAssertOk(dto, 1L, dto::setCashbackLimit, p -> p::setCashbackLimit);
    }

    @Test
    void deleteByIdTest() {
        AcquirerDto acquirerDto = acquirerGenerator.createAndSave(1, Set.of(preparedPaymentSystemId1));
        List<Long> preparedDRLIds = new ArrayList<>();
        IntStream.of(1, 2, 3).forEach(number -> preparedDRLIds.add(drlGenerator.createAndSave(number, null).getId()));
        ContactlessLimitDto contactlessLimitDto = contactlessLimitGenerator.createAndSave(2, preparedDRLIds);
        ContactLimitDto contactLimitDto = contactLimitGenerator.createAndSave(2);
        GeneralLimitDto generalLimitDto = generalLimitGenerator.createAndSave(2, contactLimitDto.getId(), contactlessLimitDto.getId());
        CardRangeDto cardRangeDto = cardRangeGenerator.createAndSave(2, acquirerDto.getId(), generalLimitDto.getId());
        assertNotNull(acquirerTestClient.getById(acquirerDto.getId()).getId());
        assertNotNull(cardRangeTestClient.getById(cardRangeDto.getId()).getId());
        acquirerTestClient.deleteById(acquirerDto.getId());
        acquirerTestClient.assertNotExists(acquirerDto.getId());
        cardRangeTestClient.assertNotExists(cardRangeDto.getId());
    }

    @Test
    void archiveAndRestoreByIdTest() {
        AcquirerDto dto = acquirerGenerator.createAndSave(1, Set.of(preparedPaymentSystemId1));
        assertFalse(acquirerTestClient.getById(dto.getId()).getArchived());

        acquirerTestClient.archiveById(dto.getId());
        assertTrue(acquirerTestClient.getById(dto.getId()).getArchived());
        securityManager.runAsSystemUser(() -> assertNotNull(acquirerService.getByIdOrElseThrow(dto.getId()).getLastArchivedAt()));

        acquirerTestClient.restoreById(dto.getId());
        assertFalse(acquirerTestClient.getById(dto.getId()).getArchived());
        securityManager.runAsSystemUser(() -> assertNotNull(acquirerService.getByIdOrElseThrow(dto.getId()).getLastRestoredAt()));
    }

    private void patchAvailableOperationsTest(AcquirerPatchDto patchDto, AcquirerDto dto) {
        patchAndAssertBadRequest(patchDto.toBuilder().availableOperations(List.of()).build());

        patchAndAssertOk(dto, List.of(SALE, AcquirerOperationType.REFUND), dto::setAvailableOperations,
                p -> value -> p.setAvailableOperations(List.of(SALE, AcquirerOperationType.REFUND)));

        dto.setAvailableOperations(List.of(AcquirerOperationType.REFUND));
        assertNotEquals(dto, acquirerTestClient.getById(dto.getId()));
        assertEquals(dto, acquirerTestClient.patch(patchDto.toBuilder()
                .availableOperations(List.of(AcquirerOperationType.REFUND))
                .build()));

        dto.setAvailableOperations(List.of(AcquirerOperationType.REFUND, SALE));
        assertNotEquals(dto, acquirerTestClient.getById(dto.getId()));
        assertEquals(dto, acquirerTestClient.patch(patchDto.toBuilder()
                .availableOperations(List.of(AcquirerOperationType.REFUND, SALE))
                .build()));
    }

    private void patchAvailableCurrenciesTest(AcquirerPatchDto patchDto, AcquirerDto dto) {
        patchAndAssertBadRequest(patchDto.toBuilder().availableCurrencies(new SetPatch<>(Set.of(), REPLACE)).build());
        patchAndAssertBadRequest(patchDto.toBuilder().availableCurrencies(new SetPatch<>(dto.getAvailableCurrencies().stream()
                .map(CurrencyDto::getSystemCode).map(CurrencyCode::valueOf).collect(toSet()), REMOVE)).build());

        patchAndAssertOk(dto, Set.of(CurrencyCode.USD, CurrencyCode.RUB), v -> dto.setAvailableCurrencies(v.stream().map(CurrencyDto::of).collect(toSet())),
                p -> value -> p.setAvailableCurrencies(new SetPatch<>(value.orElse(null), REPLACE)));

        dto.setAvailableCurrencies(Set.of(CurrencyDto.of(CurrencyCode.USD)));
        assertNotEquals(dto, acquirerTestClient.getById(dto.getId()));
        assertEquals(dto, acquirerTestClient.patch(patchDto.toBuilder()
                .availableCurrencies(new SetPatch<>(Set.of(CurrencyCode.RUB), REMOVE))
                .build()));

        dto.setAvailableCurrencies(Set.of(CurrencyDto.of(CurrencyCode.USD), CurrencyDto.of(CurrencyCode.RUB)));
        assertNotEquals(dto, acquirerTestClient.getById(dto.getId()));
        assertEquals(dto, acquirerTestClient.patch(patchDto.toBuilder()
                .availableCurrencies(new SetPatch<>(Set.of(CurrencyCode.RUB), ADD))
                .build()));
    }

    private void patchPaymentSystemsTest(AcquirerPatchDto patchDto, AcquirerDto dto) {
        Set<Long> allPaymentSystemIds = dto.getPaymentSystemsShortInfo().stream()
                .map(AcquirerPaymentSystemDto::getId)
                .collect(toSet());
        patchAndAssertBadRequest(patchDto.toBuilder().paymentSystemsIds(new SetPatch<>(Set.of(), REPLACE)).build());
        patchAndAssertBadRequest(patchDto.toBuilder().paymentSystemsIds(new SetPatch<>(allPaymentSystemIds, REMOVE)).build());

        var acquirerPaymentSystem1 = AcquirerPaymentSystemDto.builder()
                .id(preparedPaymentSystemId1)
                .name(paymentSystemTestClient.getById(preparedPaymentSystemId1).getName())
                .build();
        var acquirerPaymentSystem2 = AcquirerPaymentSystemDto.builder()
                .id(preparedPaymentSystemId2)
                .name(paymentSystemTestClient.getById(preparedPaymentSystemId2).getName())
                .build();
        patchAndAssertOk(dto, Set.of(preparedPaymentSystemId1, preparedPaymentSystemId2),
                it -> dto.setPaymentSystemsShortInfo(Set.of(acquirerPaymentSystem1, acquirerPaymentSystem2)),
                p -> value -> p.setPaymentSystemsIds(new SetPatch<>(value.orElse(null), REPLACE)));

        dto.setPaymentSystemsShortInfo(Set.of(acquirerPaymentSystem1));
        assertNotEquals(dto, acquirerTestClient.getById(dto.getId()));
        assertEquals(dto, acquirerTestClient.patch(patchDto.toBuilder()
                .paymentSystemsIds(new SetPatch<>(Set.of(preparedPaymentSystemId2), REMOVE))
                .build()));

        dto.setPaymentSystemsShortInfo(Set.of(acquirerPaymentSystem1, acquirerPaymentSystem2));
        assertNotEquals(dto, acquirerTestClient.getById(dto.getId()));
        assertEquals(dto, acquirerTestClient.patch(patchDto.toBuilder()
                .paymentSystemsIds(new SetPatch<>(Set.of(preparedPaymentSystemId2), ADD))
                .build()));
    }

    private void createAndAssertBadRequest(AcquirerCreateDto createDto) {
        assertThrowsResponseStatusException(() -> acquirerTestClient.create(createDto), HttpStatus.BAD_REQUEST);
    }

    private void assertCreateDtoMatchesDto(AcquirerCreateDto createDto, AcquirerDto dto) {
        assertEquals(createDto.getName(), dto.getName());
        assertEquals(createDto.getAvailableOperations(), new ArrayList<>(dto.getAvailableOperations()));
        assertEquals(createDto.getAvailableCurrencies(), dto.getAvailableCurrencies().stream()
                .map(CurrencyDto::getSystemCode)
                .map(CurrencyCode::valueOf)
                .collect(toSet()));
        assertEquals(createDto.getIsoProtocol(), IsoProtocol.valueOf(dto.getIsoProtocol().getSystemCode()));
        assertEquals(createDto.getUsingMacToSignRequests(), dto.getUsingMacToSignRequests());
        assertEquals(createDto.getPaymentSystemsIds(), dto.getPaymentSystemsShortInfo().stream()
                .map(AcquirerPaymentSystemDto::getId).collect(toSet()));
        assertEquals(createDto.getKeySettings().getPinMasterKeyIndex(), dto.getKeySettings().getPinMasterKeyIndex());
        assertEquals(createDto.getKeySettings().getPinWorkKeyIndex(), dto.getKeySettings().getPinWorkKeyIndex());
        assertEquals(createDto.getKeySettings().getMacMasterKeyIndex(), dto.getKeySettings().getMacMasterKeyIndex());
        assertEquals(createDto.getKeySettings().getMacWorkingKeyIndex(), dto.getKeySettings().getMacWorkingKeyIndex());
        assertEquals(createDto.getPaymentHost().getHost(), dto.getPaymentHost().getHost());
        assertEquals(createDto.getPaymentHost().getPort(), dto.getPaymentHost().getPort());
        assertEquals(createDto.getPaymentHost().getUseSsl(), dto.getPaymentHost().getUseSsl());
    }

    private <T> void patchAndAssertOk(AcquirerDto dto,
                                      T newValue,
                                      Consumer<T> dtoSetter,
                                      Function<AcquirerPatchDto, Consumer<Optional<T>>> patchDtoSetterSupplier) {
        assertEquals(dto, acquirerTestClient.getById(dto.getId()));
        dtoSetter.accept(newValue);
        assertNotEquals(dto, acquirerTestClient.getById(dto.getId()));
        AcquirerPatchDto patchDto = AcquirerPatchDto.builder().id(dto.getId()).build();
        patchDtoSetterSupplier.apply(patchDto).accept(Optional.of(newValue));
        assertEquals(dto, acquirerTestClient.patch(patchDto));
        assertEquals(dto, acquirerTestClient.getById(dto.getId()));
    }

    private void patchAndAssertBadRequest(AcquirerPatchDto patchDto) {
        assertThrowsResponseStatusException(() -> acquirerTestClient.patch(patchDto), HttpStatus.BAD_REQUEST);
    }
}
