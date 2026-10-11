package com.tms.profilemanager.app.integration;

import com.tms.profilemanager.app.entity.dictionary.CardType;
import com.tms.profilemanager.app.service.CardTypeService;
import com.tms.profilemanager.dto.BaseDto;
import com.tms.profilemanager.dto.CardTypeDto;
import com.tms.profilemanager.dto.create.dictionary.CardTypeCreateDto;
import com.tms.profilemanager.dto.filter.CardTypeFilterDto;
import com.tms.profilemanager.dto.patch.dictionary.CardTypePatchDto;
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
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.clearInvocations;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class CardTypeIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    CardTypeService cardTypeService;

    @BeforeEach
    public void beforeEach() {
        databaseCleaner.clearDatabaseAndExecuteJavaMigrations();
        requestSender.setCurrentUser(ADMIN);
    }

    @Test
    void getByIdTest() {
        CardTypeDto dto = cardTypeGenerator.createAndSave(1);
        assertEquals(dto, cardTypeTestClient.getById(dto.getId()));
    }

    @Test
    void cacheGetAllTest() {
        clearInvocations(cardTypeRepository);

        // get from database because cache is empty
        assertTrue(cardTypeService.getCardTypeUsingCacheWithoutAccessControl().isEmpty());
        verify(cardTypeRepository, times(1)).findAll();

        // get from database because result.size() == 0 (empty list are not cached)
        assertTrue(cardTypeService.getCardTypeUsingCacheWithoutAccessControl().isEmpty());
        verify(cardTypeRepository, times(2)).findAll();

        // create new card type
        cardTypeGenerator.createAndSave(1);
        cardTypeGenerator.createAndSave(2);
        List<CardType> createdCardType = cardTypeRepository.findAll();

        clearInvocations(cardTypeRepository);

        // get from database because cache is empty
        assertEquals(createdCardType, cardTypeService.getCardTypeUsingCacheWithoutAccessControl());
        verify(cardTypeRepository, times(1)).findAll();

        // get from cache and get the same result
        assertEquals(2, cardTypeService.getCardTypeUsingCacheWithoutAccessControl().size());
        verify(cardTypeRepository, times(1)).findAll();

        // patch card type
        cardTypeTestClient.patch(CardTypePatchDto.builder()
                .id(createdCardType.get(0).getId())
                .name(Optional.of("newname"))
                .build());
        List<CardType> patchedCardType = cardTypeRepository.findAll();
        assertEquals("newname", patchedCardType.stream()
                .filter(it -> it.getId().equals(createdCardType.get(0).getId()))
                .findFirst().get().getName());

        clearInvocations(cardTypeRepository);

        // get from database because cache is empty
        assertEquals(patchedCardType, cardTypeService.getCardTypeUsingCacheWithoutAccessControl());
        verify(cardTypeRepository, times(1)).findAll();

        // get from cache and get the same result
        assertEquals(patchedCardType, cardTypeService.getCardTypeUsingCacheWithoutAccessControl());
        verify(cardTypeRepository, times(1)).findAll();

        // delete card type
        cardTypeTestClient.deleteById(createdCardType.get(0).getId());

        clearInvocations(cardTypeRepository);

        // get from database because cache is empty
        assertEquals(1, cardTypeService.getCardTypeUsingCacheWithoutAccessControl().size());
        verify(cardTypeRepository, times(1)).findAll();
    }
    @Test
    void searchTest() {
        cardTypeGenerator.createAndSave(1);
        cardTypeGenerator.createAndSave(2);
        cardTypeGenerator.createAndSave(3);

        CardTypeFilterDto filterDto = new CardTypeFilterDto();
        List<Long> ids = cardTypeTestClient.search(filterDto).stream().map(BaseDto::getId).collect(toList());
        assertEquals(3, ids.size());
        filterDto.setIds(new HashSet<>(ids.subList(0, 2)));
        assertEquals(2, cardTypeTestClient.search(filterDto).getTotalElements());
    }

    @Test
    void createTest() {
        CardTypeCreateDto createDto = cardTypeGenerator.buildCreateDto(1);

        createAndAssertBadRequest(createDto.toBuilder().name(null).build());

        CardTypeDto dto = cardTypeTestClient.create(createDto);
        assertCreateDtoMatchesDto(createDto, dto);
        CardTypeDto cardTypeDto = cardTypeTestClient.getById(dto.getId());
        assertCreateDtoMatchesDto(createDto, cardTypeDto);
    }

    @Test
    void patchTest() {
        CardTypeDto dto = cardTypeGenerator.createAndSave(1);
        CardTypePatchDto patchDto = CardTypePatchDto.builder().id(dto.getId()).build();

        patchAndAssertBadRequest(patchDto.toBuilder().name(Optional.empty()).build());

        Long cardTypeId = cardTypeGenerator.createAndSave(2).getId();
        CardTypeDto cardTypeDto = cardTypeTestClient.patch(CardTypePatchDto.builder().id(cardTypeId)
                .name(Optional.of("new")).build());
        assertEquals("new", cardTypeDto.getName());

    }

    @Test
    void deleteByIdTest() {
        CardTypeDto dto = cardTypeGenerator.createAndSave(1);
        assertNotNull(cardTypeTestClient.getById(dto.getId()).getId());
        cardTypeTestClient.deleteById(dto.getId());
        cardTypeTestClient.assertNotExists(dto.getId());
    }

    private void createAndAssertBadRequest(CardTypeCreateDto createDto) {
        assertThrowsResponseStatusException(() -> cardTypeTestClient.create(createDto), HttpStatus.BAD_REQUEST);
    }

    private void assertCreateDtoMatchesDto(CardTypeCreateDto createDto, CardTypeDto dto) {
        assertEquals(createDto.getName(), dto.getName());
    }

    private <T> void patchAndAssertOk(CardTypeDto dto,
                                      T newValue,
                                      Consumer<T> dtoSetter,
                                      Function<CardTypePatchDto, Consumer<Optional<T>>> patchDtoSetterSupplier) {
        assertEquals(dto, cardTypeTestClient.getById(dto.getId()));
        dtoSetter.accept(newValue);
        assertNotEquals(dto, cardTypeTestClient.getById(dto.getId()));
        CardTypePatchDto patchDto = CardTypePatchDto.builder().id(dto.getId()).build();
        patchDtoSetterSupplier.apply(patchDto).accept(Optional.of(newValue));
        assertEquals(dto, cardTypeTestClient.patch(patchDto));
        assertEquals(dto, cardTypeTestClient.getById(dto.getId()));
    }

    private void patchAndAssertBadRequest(CardTypePatchDto patchDto) {
        assertThrowsResponseStatusException(() -> cardTypeTestClient.patch(patchDto), HttpStatus.BAD_REQUEST);
    }


}
