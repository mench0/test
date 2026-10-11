package com.tms.profilemanager.app.integration;

import com.tms.profilemanager.app.entity.dictionary.CardCode;
import com.tms.profilemanager.app.repository.CardCodeRepository;
import com.tms.profilemanager.app.service.CardCodeService;
import com.tms.profilemanager.dto.BaseDto;
import com.tms.profilemanager.dto.CardCodeDto;
import com.tms.profilemanager.dto.create.dictionary.CardCodeCreateDto;
import com.tms.profilemanager.dto.filter.CardCodeFilterDto;
import com.tms.profilemanager.dto.patch.dictionary.CardCodePatchDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.clearInvocations;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class CardCodeIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    CardCodeService cardCodeService;

    Long cardTypeId;

    @BeforeEach
    public void beforeEach() {
        databaseCleaner.clearDatabaseAndExecuteJavaMigrations();
        cardTypeId = cardTypeGenerator.createAndSave(1).getId();
        requestSender.setCurrentUser(ADMIN);
    }

    @Test
    void getByIdTest() {
        CardCodeDto dto = cardCodeGenerator.createAndSave(1, cardTypeId);
        assertEquals(dto, cardCodeTestClient.getById(dto.getId()));
    }

    @Test
    void cacheGetAllTest() {
        clearInvocations(cardCodeRepository);

        // get from database because cache is empty
        assertTrue(cardCodeService.getCardCodesUsingCacheWithoutAccessControl().isEmpty());
        verify(cardCodeRepository, times(1)).findAll();

        // get from database because result.size() == 0 (empty list are not cached)
        assertTrue(cardCodeService.getCardCodesUsingCacheWithoutAccessControl().isEmpty());
        verify(cardCodeRepository, times(2)).findAll();

        // create new card code
        cardCodeGenerator.createAndSave(1, cardTypeId);
        cardCodeGenerator.createAndSave(2, cardTypeId);
        List<CardCode> createdCardCode = cardCodeRepository.findAll();

        clearInvocations(cardCodeRepository);

        // get from database because cache is empty
        assertEquals(createdCardCode, cardCodeService.getCardCodesUsingCacheWithoutAccessControl());
        verify(cardCodeRepository, times(1)).findAll();

        // get from cache and get the same result
        assertEquals(2, cardCodeService.getCardCodesUsingCacheWithoutAccessControl().size());
        verify(cardCodeRepository, times(1)).findAll();

        // patch card code
        cardCodeTestClient.patch(CardCodePatchDto.builder()
                .id(createdCardCode.get(0).getId())
                .code(Optional.of("AA"))
                .cardTypeId(Optional.of(cardTypeId))
                .build());
        List<CardCode> patchedCardType = cardCodeRepository.findAll();
        assertEquals("AA", patchedCardType.stream()
                .filter(it -> it.getId().equals(createdCardCode.get(0).getId()))
                .findFirst().get().getCode());

        clearInvocations(cardCodeRepository);

        // get from database because cache is empty
        assertEquals(patchedCardType, cardCodeService.getCardCodesUsingCacheWithoutAccessControl());
        verify(cardCodeRepository, times(1)).findAll();

        // get from cache and get the same result
        assertEquals(patchedCardType, cardCodeService.getCardCodesUsingCacheWithoutAccessControl());
        verify(cardCodeRepository, times(1)).findAll();

        // delete card code
        cardCodeTestClient.deleteById(createdCardCode.get(0).getId());

        clearInvocations(cardCodeRepository);

        // get from database because cache is empty
        assertEquals(1, cardCodeService.getCardCodesUsingCacheWithoutAccessControl().size());
        verify(cardCodeRepository, times(1)).findAll();
    }
    @Test
    void searchTest() {
        cardCodeGenerator.createAndSave(1, cardTypeId);
        cardCodeGenerator.createAndSave(2, cardTypeId);
        cardCodeGenerator.createAndSave(3, cardTypeId);

        CardCodeFilterDto filterDto = new CardCodeFilterDto();
        List<Long> ids = cardCodeTestClient.search(filterDto).stream().map(BaseDto::getId).collect(toList());
        assertEquals(3, ids.size());
        filterDto.setIds(new HashSet<>(ids.subList(0, 2)));
        assertEquals(2, cardCodeTestClient.search(filterDto).getTotalElements());
    }

    @Test
    void groupedCodesSearchTest() {
        cardCodeGenerator.createAndSave(1, cardTypeId);
        cardCodeGenerator.createAndSave(2, cardTypeId);
        cardCodeGenerator.createAndSave(3, cardTypeId);

        Long cardTypeId2 = cardTypeGenerator.createAndSave(2).getId();
        cardCodeGenerator.createAndSave(4, cardTypeId2);

        clearInvocations(cardCodeRepository);

        List<CardCodeDto.CardTypeCodesDto> groupedCodeTypes = cardCodeTestClient.getGroupedCardCodes();

        assertEquals(2, groupedCodeTypes.size());

        List<CardCodeDto.CardTypeCodesDto> cached1 = cardCodeTestClient.getGroupedCardCodes();
        assertEquals(groupedCodeTypes, cached1);
    }

    @Test
    void cacheGroupedCardCodesTest() {
        clearInvocations(cardCodeRepository);

        // get from database because cache is empty
        assertTrue(cardCodeService.getCardTypeCodes().isEmpty());
        verify(cardCodeRepository, times(1)).findDistinctCardTypeNames();
        verify(cardCodeRepository, times(1)).findByCardTypeNamesOrdered(any());

        // get from database because result.size() == 0 (empty list are not cached)
        assertTrue(cardCodeService.getCardTypeCodes().isEmpty());
        verify(cardCodeRepository, times(2)).findDistinctCardTypeNames();
        verify(cardCodeRepository, times(2)).findByCardTypeNamesOrdered(any());

        // create new card code
        cardCodeGenerator.createAndSave(1, cardTypeId);
        cardCodeGenerator.createAndSave(2, cardTypeId);
        cardCodeGenerator.createAndSave(3, cardTypeId);

        Long cardTypeId2 = cardTypeGenerator.createAndSave(2).getId();
        cardCodeGenerator.createAndSave(4, cardTypeId2);
        List<String> createdCardCodeNames = cardCodeRepository.findDistinctCardTypeNames();
        List<CardCodeRepository.CardCodeSlim> createdCardCode = cardCodeRepository.findByCardTypeNamesOrdered(createdCardCodeNames);

        clearInvocations(cardCodeRepository);

        // get from database because cache is empty
        assertEquals(createdCardCodeNames.stream().sorted().toList(),
                cardCodeService.getCardTypeCodes().stream().map(CardCodeDto.CardTypeCodesDto::getCardType).sorted().toList());
        verify(cardCodeRepository, times(1)).findDistinctCardTypeNames();
        verify(cardCodeRepository, times(1)).findByCardTypeNamesOrdered(createdCardCodeNames);

        // get from cache and get the same result
        assertEquals(2, cardCodeService.getCardTypeCodes().size());
        verify(cardCodeRepository, times(1)).findDistinctCardTypeNames();
        verify(cardCodeRepository, times(1)).findByCardTypeNamesOrdered(createdCardCodeNames);

        // patch card code
        CardCodeFilterDto filterDto = new CardCodeFilterDto();
        List<Long> ids = cardCodeTestClient.search(filterDto).stream().map(BaseDto::getId).toList();
        cardCodeTestClient.patch(CardCodePatchDto.builder()
                .id(ids.get(0))
                .code(Optional.of("AA"))
                .cardTypeId(Optional.of(cardTypeId))
                .build());
        List<CardCode> patchedCardType = cardCodeRepository.findAll();
        Map<String, List<CardCode>> grouped = patchedCardType.stream()
                .collect(Collectors.groupingBy(c -> c.getCardType().getName()));

        List<CardCodeDto.CardTypeCodesDto> groupesCardCodes = grouped.entrySet().stream()
                .sorted(Comparator.comparing(e -> e.getValue().get(0).getCardType().getCreatedAt()))
                .map(e -> {
                    String cardTypeName = e.getKey();
                    List<CardCode> codes = e.getValue();

                    Long id = codes.get(0).getCardType().getId();
                    List<String> sortedCodes = codes.stream()
                            .map(CardCode::getCode)
                            .sorted()
                            .toList();

                    return CardCodeDto.CardTypeCodesDto.builder()
                            .id(id)
                            .cardType(cardTypeName)
                            .codes(sortedCodes)
                            .build();
                })
                .toList().stream()
                .sorted(Comparator.comparing(CardCodeDto.CardTypeCodesDto::getId))
                .toList();

        assertEquals("AA", patchedCardType.stream()
                .filter(it -> it.getId().equals(ids.get(0)))
                .findFirst().get().getCode());

        clearInvocations(cardCodeRepository);


        // get from database because cache is empty
        assertEquals(groupesCardCodes, cardCodeService.getCardTypeCodes().stream()
                .sorted(Comparator.comparing(CardCodeDto.CardTypeCodesDto::getId))
                .toList());
        verify(cardCodeRepository, times(1)).findDistinctCardTypeNames();
        verify(cardCodeRepository, times(1)).findByCardTypeNamesOrdered(createdCardCodeNames);

        // get from cache and get the same result
        assertEquals(groupesCardCodes, cardCodeService.getCardTypeCodes().stream()
                .sorted(Comparator.comparing(CardCodeDto.CardTypeCodesDto::getId))
                .toList());
        verify(cardCodeRepository, times(1)).findDistinctCardTypeNames();
        verify(cardCodeRepository, times(1)).findByCardTypeNamesOrdered(createdCardCodeNames);

        // delete card code
        cardCodeTestClient.deleteById(ids.get(0));

        clearInvocations(cardCodeRepository);

        // get from database because cache is empty
        assertEquals(2, cardCodeService.getCardTypeCodes().stream()
                .sorted(Comparator.comparing(CardCodeDto.CardTypeCodesDto::getId))
                .toList().size());
        verify(cardCodeRepository, times(1)).findDistinctCardTypeNames();
        verify(cardCodeRepository, times(1)).findByCardTypeNamesOrdered(createdCardCodeNames);
    }

    @Test
    void createTest() {
        CardCodeCreateDto createDto = cardCodeGenerator.buildCreateDto(1, cardTypeId);

        createAndAssertBadRequest(createDto.toBuilder().code(null).build());
        createAndAssertBadRequest(createDto.toBuilder().code("ABC").build());

        CardCodeDto dto = cardCodeTestClient.create(createDto);
        assertCreateDtoMatchesDto(createDto, dto);
        CardCodeDto cardCodeDto = cardCodeTestClient.getById(dto.getId());
        assertCreateDtoMatchesDto(createDto, cardCodeDto);
    }

    @Test
    void patchTest() {
        CardCodePatchDto patchDto = CardCodePatchDto.builder().id(cardTypeId).build();

        patchAndAssertBadRequest(patchDto.toBuilder().code(Optional.empty()).build());
        patchAndAssertBadRequest(patchDto.toBuilder().code(Optional.of("DYF")).build());

        Long cardCodeId = cardCodeGenerator.createAndSave(2, cardTypeId).getId();
        CardCodeDto cardCodeDto = cardCodeTestClient.patch(CardCodePatchDto.builder()
                .id(cardCodeId)
                .code(Optional.of("BB")).build());
        assertEquals("BB", cardCodeDto.getCode());

    }

    @Test
    void deleteByIdTest() {
        CardCodeDto dto = cardCodeGenerator.createAndSave(1, cardTypeId);
        assertNotNull(cardCodeTestClient.getById(dto.getId()).getId());
        cardCodeTestClient.deleteById(dto.getId());
        cardCodeTestClient.assertNotExists(dto.getId());
    }

    private void createAndAssertBadRequest(CardCodeCreateDto createDto) {
        assertThrowsResponseStatusException(() -> cardCodeTestClient.create(createDto), HttpStatus.BAD_REQUEST);
    }

    private void assertCreateDtoMatchesDto(CardCodeCreateDto createDto, CardCodeDto dto) {
        assertEquals(createDto.getCode(), dto.getCode());
    }

    private <T> void patchAndAssertOk(CardCodeDto dto,
                                      T newValue,
                                      Consumer<T> dtoSetter,
                                      Function<CardCodePatchDto, Consumer<Optional<T>>> patchDtoSetterSupplier) {
        assertEquals(dto, cardCodeTestClient.getById(dto.getId()));
        dtoSetter.accept(newValue);
        assertNotEquals(dto, cardCodeTestClient.getById(dto.getId()));
        CardCodePatchDto patchDto = CardCodePatchDto.builder().id(dto.getId()).build();
        patchDtoSetterSupplier.apply(patchDto).accept(Optional.of(newValue));
        assertEquals(dto, cardCodeTestClient.patch(patchDto));
        assertEquals(dto, cardCodeTestClient.getById(dto.getId()));
    }

    private void patchAndAssertBadRequest(CardCodePatchDto patchDto) {
        assertThrowsResponseStatusException(() -> cardCodeTestClient.patch(patchDto), HttpStatus.BAD_REQUEST);
    }


}
