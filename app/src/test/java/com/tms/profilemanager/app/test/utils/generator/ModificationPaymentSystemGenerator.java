package com.tms.profilemanager.app.test.utils.generator;

import com.tms.profilemanager.app.config.security.RunAsSystemUser;
import com.tms.profilemanager.app.controller.ModificationPaymentSystemController;
import com.tms.profilemanager.dto.create.modification.ModificationPaymentSystemCreateDto;
import com.tms.profilemanager.dto.modification.ModificationPaymentSystemDto;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Component
@Transactional
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ModificationPaymentSystemGenerator {

    ModificationPaymentSystemController modificationPaymentSystemController;

    @RunAsSystemUser
    public ModificationPaymentSystemDto createAndSave(int number, Long paymentSystemId, Long groupId, Set<Long> limitIds) {
        return modificationPaymentSystemController.create(buildCreateDto(number, paymentSystemId, groupId, limitIds));
    }

    public ModificationPaymentSystemCreateDto buildCreateDto(int number, Long paymentSystemId, Long groupId, Set<Long> limitIds) {
        return ModificationPaymentSystemCreateDto.builder()
                .avn(number + "1")
                .contactlessAvn(number + "1")
                .paymentSystemId(paymentSystemId)
                .groupId(groupId)
                .limits(limitIds)
                .build();
    }
}
