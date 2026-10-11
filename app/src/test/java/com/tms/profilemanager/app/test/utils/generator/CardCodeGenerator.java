package com.tms.profilemanager.app.test.utils.generator;

import com.tms.profilemanager.app.config.security.RunAsSystemUser;
import com.tms.profilemanager.app.controller.CardCodeController;
import com.tms.profilemanager.dto.CardCodeDto;
import com.tms.profilemanager.dto.create.dictionary.CardCodeCreateDto;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Transactional
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CardCodeGenerator {

    CardCodeController cardCodeController;

    @RunAsSystemUser
    public CardCodeDto createAndSave(int number, Long cardTypeId) {
        return cardCodeController.create(buildCreateDto(number, cardTypeId));
    }

    public CardCodeCreateDto buildCreateDto(int number, Long cardTypeId) {
        return CardCodeCreateDto.builder()
                .code("A" + number)
                .cardTypeId(cardTypeId)
                .build();
    }
}
