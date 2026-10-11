package com.tms.profilemanager.dto;

import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.FieldNameConstants;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@FieldDefaults(level = AccessLevel.PROTECTED)
@FieldNameConstants
public class GeneralLimitDto extends BaseDto {

    String name;

    ContactLimitDto contactLimit;

    ContactlessLimitDto contactlessLimit;
}
