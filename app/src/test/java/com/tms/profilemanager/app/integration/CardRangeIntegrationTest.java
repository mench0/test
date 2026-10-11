package com.tms.profilemanager.app.integration;

import com.tms.profilemanager.app.entity.BaseEntity;
import com.tms.profilemanager.app.entity.CardRange;
import com.tms.profilemanager.app.service.CardRangeService;
import com.tms.profilemanager.dto.BaseDto;
import com.tms.profilemanager.dto.CardRangeDto;
import com.tms.profilemanager.dto.create.CardRangeCreateDto;
import com.tms.profilemanager.dto.filter.CardRangeFilterDto;
import com.tms.profilemanager.dto.patch.CardRangePatchDto;
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

import static java.util.stream.Collectors.toList;
import static org.apache.commons.lang3.StringUtils.repeat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.clearInvocations;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

class CardRangeIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    CardRangeService cardRangeService;

    Long preparedPaymentSystemId;
    Long acquirerId;
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
        acquirerId = acquirerGenerator.createAndSave(1, Set.of(preparedPaymentSystemId)).getId();
        requestSender.setCurrentUser(ADMIN);
    }

    @Test
    void getByIdTest() {
        CardRangeDto dto = createAndSaveCardRange(1);
        assertEquals(dto, cardRangeTestClient.getById(dto.getId()));
    }

    @Test
    void cacheByAcquirerIdTest() {
        clearInvocations(cardRangeRepository);

        // get from database because cache is empty
        assertTrue(cardRangeService.getByAcquirerIdUsingCacheWithoutAccessControl(1L).isEmpty());
        verify(cardRangeRepository, times(1)).getByAcquirerId(1L);

        // get from cache
        assertTrue(cardRangeService.getByAcquirerIdUsingCacheWithoutAccessControl(1L).isEmpty());
        verify(cardRangeRepository, times(2)).getByAcquirerId(1L);

        // create new payment system
        Long cardRangeId = cardRangeGenerator.createAndSave(1, acquirerId, preparedGeneralLimitId).getId();
        CardRange createdCardRange = cardRangeRepository.findById(cardRangeId).orElseThrow();

        clearInvocations(cardRangeRepository);

        // get from database because cache is empty
        assertEquals(createdCardRange, cardRangeService.getByAcquirerIdUsingCacheWithoutAccessControl(acquirerId).get(0));
        verify(cardRangeRepository, times(1)).getByAcquirerId(acquirerId);

        // get from cache and get the same result
        assertEquals(createdCardRange, cardRangeService.getByAcquirerIdUsingCacheWithoutAccessControl(acquirerId).get(0));
        verify(cardRangeRepository, times(1)).getByAcquirerId(acquirerId);

        // patch payment system
        cardRangeTestClient.patch(CardRangePatchDto.builder()
                .id(createdCardRange.getId())
                .name(Optional.of("New name"))
                .limits(createdCardRange.getLimits().stream().map(BaseEntity::getId).collect(Collectors.toSet()))
                .build());
        CardRange patchedCardRange = cardRangeRepository.findById(cardRangeId).orElseThrow();
        assertNotEquals(createdCardRange, patchedCardRange);

        clearInvocations(cardRangeRepository);

        // get from database because cache is empty
        assertEquals(patchedCardRange, cardRangeService.getByAcquirerIdUsingCacheWithoutAccessControl(acquirerId).get(0));
        verify(cardRangeRepository, times(1)).getByAcquirerId(acquirerId);

        // get from cache and get the same result
        assertEquals(patchedCardRange, cardRangeService.getByAcquirerIdUsingCacheWithoutAccessControl(acquirerId).get(0));
        verify(cardRangeRepository, times(1)).getByAcquirerId(acquirerId);

        // delete payment system
        cardRangeTestClient.deleteById(cardRangeId);

        clearInvocations(cardRangeRepository);

        // get from database because cache is empty
        assertTrue(cardRangeService.getByAcquirerIdUsingCacheWithoutAccessControl(acquirerId).isEmpty());
        verify(cardRangeRepository, times(1)).getByAcquirerId(any(Long.class));
    }

    @Test
    void searchTest() {
        Long acquirerId = acquirerGenerator.createAndSave(2, Set.of(preparedPaymentSystemId)).getId();
        cardRangeGenerator.createAndSave(1, acquirerId, preparedGeneralLimitId);
        cardRangeGenerator.createAndSave(2, acquirerId, preparedGeneralLimitId);
        cardRangeGenerator.createAndSave(3, acquirerId, preparedGeneralLimitId);

        CardRangeFilterDto filterDto = new CardRangeFilterDto();
        List<Long> ids = cardRangeTestClient.search(filterDto).stream().map(BaseDto::getId).collect(toList());
        assertEquals(3, ids.size());
        filterDto.setIds(new HashSet<>(ids.subList(0, 2)));
        assertEquals(2, cardRangeTestClient.search(filterDto).getTotalElements());

        filterDto.setIds(null);
        assertEquals(3, cardRangeTestClient.search(filterDto).getTotalElements());
    }

    @Test
    void createTest() {
        CardRangeCreateDto createDto = cardRangeGenerator.buildCreateDto(
                1, acquirerId, preparedGeneralLimitId);

        createAndAssertBadRequest(createDto.toBuilder().low(null).build());
        createAndAssertBadRequest(createDto.toBuilder().low("").build());
        createAndAssertBadRequest(createDto.toBuilder().low(repeat("*", 20)).build());

        createAndAssertBadRequest(createDto.toBuilder().high(null).build());
        createAndAssertBadRequest(createDto.toBuilder().high("").build());
        createAndAssertBadRequest(createDto.toBuilder().high(repeat("*", 20)).build());

        createAndAssertBadRequest(createDto.toBuilder().name(null).build());
        createAndAssertBadRequest(createDto.toBuilder().name("").build());
        createAndAssertBadRequest(createDto.toBuilder().name(repeat("*", 101)).build());

        createAndAssertBadRequest(createDto.toBuilder().acquirerId(null).build());
        createAndAssertBadRequest(createDto.toBuilder().acquirerId(-1L).build());

        createAndAssertBadRequest(createDto.toBuilder().limits(Collections.emptySet()).build());

        CardRangeDto dto = cardRangeTestClient.create(createDto);
        assertCreateDtoMatchesDto(createDto, dto);
        assertCreateDtoMatchesDto(createDto, cardRangeTestClient.getById(dto.getId()));
    }

    @Test
    void patchTest() {
        CardRangeDto dto = createAndSaveCardRange(1);
        CardRangePatchDto patchDto = CardRangePatchDto.builder().id(dto.getId()).build();

        patchAndAssertBadRequest(patchDto.toBuilder().low(Optional.empty()).build());
        patchAndAssertBadRequest(patchDto.toBuilder().low(Optional.of("")).build());
        patchAndAssertBadRequest(patchDto.toBuilder().low(Optional.of(repeat("*", 20))).build());
        CardRangeDto lowPatch = cardRangeTestClient.patch(CardRangePatchDto.builder().id(dto.getId())
                .low(Optional.of(repeat("a", 14)))
                .limits(dto.getLimits().stream().map(BaseDto::getId).collect(Collectors.toSet())).build());
        assertEquals(repeat("a", 14), lowPatch.getLow());

        patchAndAssertBadRequest(patchDto.toBuilder().high(Optional.empty()).build());
        patchAndAssertBadRequest(patchDto.toBuilder().high(Optional.of("")).build());
        patchAndAssertBadRequest(patchDto.toBuilder().high(Optional.of(repeat("*", 20))).build());
        CardRangeDto highPatch = cardRangeTestClient.patch(CardRangePatchDto.builder().id(dto.getId())
                .high(Optional.of(repeat("a", 14)))
                .limits(dto.getLimits().stream().map(BaseDto::getId).collect(Collectors.toSet())).build());
        assertEquals(repeat("a", 14), highPatch.getHigh());

        patchAndAssertBadRequest(patchDto.toBuilder().name(Optional.empty()).build());
        patchAndAssertBadRequest(patchDto.toBuilder().name(Optional.of("")).build());
        patchAndAssertBadRequest(patchDto.toBuilder().name(Optional.of(repeat("*", 101))).build());
        CardRangeDto namePatch = cardRangeTestClient.patch(CardRangePatchDto.builder().id(dto.getId())
                .name(Optional.of(repeat("a", 100)))
                .limits(dto.getLimits().stream().map(BaseDto::getId).collect(Collectors.toSet())).build());
        assertEquals(repeat("a", 100), namePatch.getName());

        patchAndAssertBadRequest(patchDto.toBuilder().acquirerId(Optional.empty()).build());
        patchAndAssertBadRequest(patchDto.toBuilder().acquirerId(Optional.of(-1L)).build());
        Long acquirerId = acquirerGenerator.createAndSave(2, Set.of(preparedPaymentSystemId)).getId();
        CardRangeDto acquirerIdPatch = cardRangeTestClient.patch(CardRangePatchDto.builder().id(dto.getId())
                .acquirerId(Optional.of(acquirerId))
                .limits(dto.getLimits().stream().map(BaseDto::getId).collect(Collectors.toSet())).build());
        assertEquals(acquirerId, acquirerIdPatch.getAcquirerId());

        patchAndAssertBadRequest(patchDto.toBuilder().limits(Collections.emptySet()).build());

    }

    @Test
    void deleteByIdTest() {
        CardRangeDto dto = createAndSaveCardRange(1);
        assertNotNull(cardRangeTestClient.getById(dto.getId()).getId());
        cardRangeTestClient.deleteById(dto.getId());
        cardRangeTestClient.assertNotExists(dto.getId());
    }

    @Test
    void archiveAndRestoreByIdTest() {
        CardRangeDto dto = createAndSaveCardRange(1);
        assertFalse(cardRangeTestClient.getById(dto.getId()).getArchived());

        cardRangeTestClient.archiveById(dto.getId());
        assertTrue(cardRangeTestClient.getById(dto.getId()).getArchived());
        securityManager.runAsSystemUser(() -> assertNotNull(cardRangeService.getByIdOrElseThrow(dto.getId()).getLastArchivedAt()));

        cardRangeTestClient.restoreById(dto.getId());
        assertFalse(cardRangeTestClient.getById(dto.getId()).getArchived());
        securityManager.runAsSystemUser(() -> assertNotNull(cardRangeService.getByIdOrElseThrow(dto.getId()).getLastRestoredAt()));
    }

    private void createAndAssertBadRequest(CardRangeCreateDto createDto) {
        assertThrowsResponseStatusException(() -> cardRangeTestClient.create(createDto), HttpStatus.BAD_REQUEST);
    }

    private void assertCreateDtoMatchesDto(CardRangeCreateDto createDto, CardRangeDto dto) {
        assertEquals(createDto.getLow(), dto.getLow());
        assertEquals(createDto.getHigh(), dto.getHigh());
        assertEquals(createDto.getAcquirerId(), dto.getAcquirerId());
        assertEquals(createDto.getLimits(), dto.getLimits().stream().map(BaseDto::getId).collect(Collectors.toSet()));
    }

    private <T> void patchAndAssertOk(CardRangeDto dto,
                                      T newValue,
                                      Consumer<T> dtoSetter,
                                      Function<CardRangePatchDto, Consumer<Optional<T>>> patchDtoSetterSupplier) {
        assertEquals(dto, cardRangeTestClient.getById(dto.getId()));
        dtoSetter.accept(newValue);
        assertNotEquals(dto, cardRangeTestClient.getById(dto.getId()));
        CardRangePatchDto patchDto = CardRangePatchDto.builder().id(dto.getId()).build();
        patchDtoSetterSupplier.apply(patchDto).accept(Optional.of(newValue));
        assertEquals(dto, cardRangeTestClient.patch(patchDto));
        assertEquals(dto, cardRangeTestClient.getById(dto.getId()));
    }

    private void patchAndAssertBadRequest(CardRangePatchDto patchDto) {
        assertThrowsResponseStatusException(() -> cardRangeTestClient.patch(patchDto), HttpStatus.BAD_REQUEST);
    }

    private CardRangeDto createAndSaveCardRange(int number) {
        return cardRangeGenerator.createAndSave(number, acquirerId, preparedGeneralLimitId);
    }
}
