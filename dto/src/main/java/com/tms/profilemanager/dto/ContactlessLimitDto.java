package com.tms.profilemanager.dto;

import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;

import java.util.List;


@Data
@SuperBuilder
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ContactlessLimitDto extends BaseDto {

    String name;

    CurrencyDto currency;

    Long floorLimit;

    Long noCvmLimit;

    Long onDeviceCvmLimit;

    Long noDeviceCvmLimit;

    List<DRLDto> drlList;

    Long maxAmount;
}
