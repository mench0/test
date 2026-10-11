package com.tms.profilemanager.app.test.utils.generator;

import com.tms.profilemanager.app.config.security.RunAsSystemUser;
import com.tms.profilemanager.app.controller.CardRangeController;
import com.tms.profilemanager.dto.CardRangeDto;
import com.tms.profilemanager.dto.create.CardRangeCreateDto;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

import static org.apache.commons.lang3.StringUtils.repeat;


@Component
@Transactional
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CardRangeGenerator {

    CardRangeController cardRangeController;

    @RunAsSystemUser
    public CardRangeDto createAndSave(int number,
                                      Long acquirerId,
                                      Long limitId) {
        return cardRangeController.create(buildCreateDto(number, acquirerId, limitId));
    }

    public CardRangeCreateDto buildCreateDto(int number,
                                             Long acquirerId,
                                             Long limitId) {
        String low = "low" + repeat("_", 12) +  number;
        String high = "high" + repeat("_", 12) + number;
        return CardRangeCreateDto.builder()
                .low(low)
                .high(high)
                .name(low + " - " + high)
                .limits(Set.of(limitId))
                .acquirerId(acquirerId)
                .build();
    }
}
