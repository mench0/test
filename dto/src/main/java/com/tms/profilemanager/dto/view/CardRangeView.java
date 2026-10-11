package com.tms.profilemanager.dto.view;

import com.tms.profilemanager.dto.BaseDto;
import com.tms.profilemanager.dto.ContactLimitDto;
import com.tms.profilemanager.dto.ContactlessLimitDto;
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
public class CardRangeView extends BaseDto {

    String low;

    String high;

    String name;

    Long acquirerId;

    ContactLimitDto contactLimit;

    ContactlessLimitDto contactlessLimit;

}
