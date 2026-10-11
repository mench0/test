package com.tms.profilemanager.dto;

import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;


@Data
@SuperBuilder
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DRLDto extends BaseDto {

    Integer index;

    Long operationLimit;

    Long floorLimit;

    Long noCvmLimit;

    Long contactlessLimitId;
}
