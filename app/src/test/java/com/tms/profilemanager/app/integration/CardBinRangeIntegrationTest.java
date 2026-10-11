package com.tms.profilemanager.app.integration;

import com.tms.profilemanager.app.entity.dictionary.CardBinRange;
import com.tms.profilemanager.app.service.CardBinRangeService;
import com.tms.profilemanager.dto.BaseDto;
import com.tms.profilemanager.dto.CardBinRangeDto;
import com.tms.profilemanager.dto.create.dictionary.CardBinRangeCreateDto;
import com.tms.profilemanager.dto.filter.CardBinRangeFilterDto;
import com.tms.profilemanager.dto.patch.dictionary.CardBinRangePatchDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

import static java.util.stream.Collectors.toList;
import static org.apache.commons.lang3.StringUtils.repeat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.clearInvocations;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class CardBinRangeIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    CardBinRangeService cardBinRangeService;

    Long cardTypeId;

    @BeforeEach
    public void beforeEach() {
        databaseCleaner.clearDatabaseAndExecuteJavaMigrations();
        cardTypeId = cardTypeGenerator.createAndSave(1).getId();
        requestSender.setCurrentUser(ADMIN);
    }

    @Test
    void getByIdTest() {
        CardBinRangeDto dto = cardBinRangeGenerator.createAndSave(1, cardTypeId);
        assertEquals(dto, cardBinRangeTestClient.getById(dto.getId()));
    }

    @Test
    void cacheGetAllTest() {
        clearInvocations(cardBinRangeRepository);

        // get from database because cache is empty
        assertTrue(cardBinRangeService.getCardBinRangesUsingCacheWithoutAccessControl().isEmpty());
        verify(cardBinRangeRepository, times(1)).findAll();

        // get from database because result.size() == 0 (empty list are not cached)
        assertTrue(cardBinRangeService.getCardBinRangesUsingCacheWithoutAccessControl().isEmpty());
        verify(cardBinRangeRepository, times(2)).findAll();

        // create new card bin range
        cardBinRangeGenerator.createAndSave(1, cardTypeId);
        cardBinRangeGenerator.createAndSave(2, cardTypeId);
        List<CardBinRange> createdCardBinRange = cardBinRangeRepository.findAll();

        clearInvocations(cardBinRangeRepository);

        // get from database because cache is empty
        assertEquals(createdCardBinRange, cardBinRangeService.getCardBinRangesUsingCacheWithoutAccessControl());
        verify(cardBinRangeRepository, times(1)).findAll();

        // get from cache and get the same result
        assertEquals(2, cardBinRangeService.getCardBinRangesUsingCacheWithoutAccessControl().size());
        verify(cardBinRangeRepository, times(1)).findAll();

        // patch card bin range
        cardBinRangeTestClient.patch(CardBinRangePatchDto.builder()
                .id(createdCardBinRange.get(0).getId())
                .startBin(Optional.of("11"))
                .endBin(Optional.of("22"))
                .cardTypeId(Optional.of(cardTypeId))
                .build());
        List<CardBinRange> patchedCardBinRange = cardBinRangeRepository.findAll();
        assertEquals("22", patchedCardBinRange.stream()
                .filter(it -> it.getId().equals(createdCardBinRange.get(0).getId()))
                .findFirst().get().getEndBin());

        clearInvocations(cardBinRangeRepository);

        // get from database because cache is empty
        assertEquals(patchedCardBinRange, cardBinRangeService.getCardBinRangesUsingCacheWithoutAccessControl());
        verify(cardBinRangeRepository, times(1)).findAll();

        // get from cache and get the same result
        assertEquals(patchedCardBinRange, cardBinRangeService.getCardBinRangesUsingCacheWithoutAccessControl());
        verify(cardBinRangeRepository, times(1)).findAll();

        // delete card bin range
        cardBinRangeTestClient.deleteById(createdCardBinRange.get(0).getId());

        clearInvocations(cardBinRangeRepository);

        // get from database because cache is empty
        assertEquals(1, cardBinRangeService.getCardBinRangesUsingCacheWithoutAccessControl().size());
        verify(cardBinRangeRepository, times(1)).findAll();
    }

    @Test
    void searchTest() {
        cardBinRangeGenerator.createAndSave(1, cardTypeId);
        cardBinRangeGenerator.createAndSave(2, cardTypeId);
        cardBinRangeGenerator.createAndSave(3, cardTypeId);

        CardBinRangeFilterDto filterDto = new CardBinRangeFilterDto();
        List<Long> ids = cardBinRangeTestClient.search(filterDto).stream().map(BaseDto::getId).collect(toList());
        assertEquals(3, ids.size());
        filterDto.setIds(new HashSet<>(ids.subList(0, 2)));
        assertEquals(2, cardBinRangeTestClient.search(filterDto).getTotalElements());
    }

    @Test
    void createTest() {
        CardBinRangeCreateDto createDto = cardBinRangeGenerator.buildCreateDto(1, cardTypeId);

        createAndAssertBadRequest(createDto.toBuilder().startBin(null).build());
        createAndAssertBadRequest(createDto.toBuilder().startBin("").build());
        createAndAssertBadRequest(createDto.toBuilder().startBin(" ").build());

        createAndAssertBadRequest(createDto.toBuilder().endBin(null).build());
        createAndAssertBadRequest(createDto.toBuilder().endBin("").build());
        createAndAssertBadRequest(createDto.toBuilder().endBin(" ").build());

        CardBinRangeDto dto = cardBinRangeTestClient.create(createDto);
        assertCreateDtoMatchesDto(createDto, dto);
        CardBinRangeDto cardBinRangeDto = cardBinRangeTestClient.getById(dto.getId());
        assertCreateDtoMatchesDto(createDto, cardBinRangeDto);
    }

    @Test
    void patchTest() {
        CardBinRangeDto dto = cardBinRangeGenerator.createAndSave(1, cardTypeId);
        CardBinRangePatchDto patchDto = CardBinRangePatchDto.builder().id(dto.getId()).build();

        patchAndAssertBadRequest(patchDto.toBuilder().startBin(Optional.empty()).build());
        patchAndAssertBadRequest(patchDto.toBuilder().startBin(Optional.of("")).build());
        patchAndAssertBadRequest(patchDto.toBuilder().startBin(Optional.of(repeat("1", 20))).build());

        patchAndAssertBadRequest(patchDto.toBuilder().endBin(Optional.empty()).build());
        patchAndAssertBadRequest(patchDto.toBuilder().endBin(Optional.of("")).build());
        patchAndAssertBadRequest(patchDto.toBuilder().endBin(Optional.of(repeat("1", 20))).build());

        Long cardBinRangeId = cardBinRangeGenerator.createAndSave(2, cardTypeId).getId();
        CardBinRangeDto cardBinRangeDto = cardBinRangeTestClient.patch(CardBinRangePatchDto.builder().id(cardBinRangeId)
                .startBin(Optional.of("1234"))
                .endBin(Optional.of("234324"))
                .cardTypeId(Optional.of(cardTypeId))
                .build());
        assertEquals("1234", cardBinRangeDto.getStartBin());
        assertEquals("234324", cardBinRangeDto.getEndBin());

    }

    @Test
    void deleteByIdTest() {
        CardBinRangeDto dto = cardBinRangeGenerator.createAndSave(1, cardTypeId);
        assertNotNull(cardBinRangeTestClient.getById(dto.getId()).getId());
        cardBinRangeTestClient.deleteById(dto.getId());
        cardBinRangeTestClient.assertNotExists(dto.getId());
    }

    private void createAndAssertBadRequest(CardBinRangeCreateDto createDto) {
        assertThrowsResponseStatusException(() -> cardBinRangeTestClient.create(createDto), HttpStatus.BAD_REQUEST);
    }

    private void assertCreateDtoMatchesDto(CardBinRangeCreateDto createDto, CardBinRangeDto dto) {
        assertEquals(createDto.getStartBin(), dto.getStartBin());
        assertEquals(createDto.getEndBin(), dto.getEndBin());
    }

    private <T> void patchAndAssertOk(CardBinRangeDto dto,
                                      T newValue,
                                      Consumer<T> dtoSetter,
                                      Function<CardBinRangePatchDto, Consumer<Optional<T>>> patchDtoSetterSupplier) {
        assertEquals(dto, cardBinRangeTestClient.getById(dto.getId()));
        dtoSetter.accept(newValue);
        assertNotEquals(dto, cardBinRangeTestClient.getById(dto.getId()));
        CardBinRangePatchDto patchDto = CardBinRangePatchDto.builder().id(dto.getId()).build();
        patchDtoSetterSupplier.apply(patchDto).accept(Optional.of(newValue));
        assertEquals(dto, cardBinRangeTestClient.patch(patchDto));
        assertEquals(dto, cardBinRangeTestClient.getById(dto.getId()));
    }

    private void patchAndAssertBadRequest(CardBinRangePatchDto patchDto) {
        assertThrowsResponseStatusException(() -> cardBinRangeTestClient.patch(patchDto), HttpStatus.BAD_REQUEST);
    }


}
