package com.tms.profilemanager.app.test.utils.generator;

import com.tms.profilemanager.app.config.security.RunAsSystemUser;
import com.tms.profilemanager.app.controller.CardBinRangeController;
import com.tms.profilemanager.dto.CardBinRangeDto;
import com.tms.profilemanager.dto.create.dictionary.CardBinRangeCreateDto;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Transactional
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CardBinRangeGenerator {

    CardBinRangeController cardBinRangeController;

    @RunAsSystemUser
    public CardBinRangeDto createAndSave(int number, Long cardTypeId) {
        return cardBinRangeController.create(buildCreateDto(number, cardTypeId));
    }

    public CardBinRangeCreateDto buildCreateDto(int number, Long cardTypeId) {
        return CardBinRangeCreateDto.builder()
                .startBin("1" + number)
                .endBin("2" + number)
                .cardTypeId(cardTypeId)
                .build();
    }
}
