package com.tms.profilemanager.dto.patch;

import com.neovisionaries.i18n.CurrencyCode;
import com.tms.profilemanager.dto.ContactLimitDto;
import com.tms.profilemanager.dto.enums.AcquirerOperationType;
import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Optional;
import java.util.Set;

@Data
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ContactLimitPatchDto extends BasePatchDto<ContactLimitDto> {

    Optional<@NotBlank String> name;

    Optional<@NotNull CurrencyCode> currency;

    Optional<@NotNull Long> threshold;

    Optional<@NotNull Integer> targetPercent;

    Optional<@NotNull Integer> maxTargetPercent;

    Optional<@NotNull Long> floorLimit;

    Optional<@NotNull Boolean> pinBypassAllowed;

    Optional<@NotNull Integer> pinLimit;

    Set<AcquirerOperationType> requestLast4DigitsPAN;

}
