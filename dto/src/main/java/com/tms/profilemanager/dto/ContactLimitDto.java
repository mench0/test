package com.tms.profilemanager.dto;

import com.tms.profilemanager.dto.enums.AcquirerOperationType;
import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;

import java.util.Set;

@Data
@SuperBuilder
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ContactLimitDto extends BaseDto {

    String name;

    CurrencyDto currency;

    Long threshold;

    Integer targetPercent;

    Integer maxTargetPercent;

    Long floorLimit;

    Boolean pinBypassAllowed;

    Integer pinLimit;

    Set<AcquirerOperationType> requestLast4DigitsPAN;
}
