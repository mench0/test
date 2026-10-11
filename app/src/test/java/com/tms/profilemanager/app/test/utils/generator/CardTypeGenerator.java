package com.tms.profilemanager.app.test.utils.generator;

import com.tms.profilemanager.app.config.security.RunAsSystemUser;
import com.tms.profilemanager.app.controller.CardTypeController;
import com.tms.profilemanager.dto.CardTypeDto;
import com.tms.profilemanager.dto.create.dictionary.CardTypeCreateDto;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Transactional
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CardTypeGenerator {

    CardTypeController cardTypeController;

    @RunAsSystemUser
    public CardTypeDto createAndSave(int number) {
        return cardTypeController.create(buildCreateDto(number));
    }

    public CardTypeCreateDto buildCreateDto(int number) {
        return CardTypeCreateDto.builder()
                .name("name" + number)
                .build();
    }
}
