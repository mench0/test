package com.tms.profilemanager.dto.modification;

import com.tms.profilemanager.dto.BaseDto;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.FieldNameConstants;
import lombok.experimental.SuperBuilder;

import java.util.Set;

@Data
@SuperBuilder
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@FieldDefaults(level = AccessLevel.PROTECTED)
@FieldNameConstants
public class ModificationPaymentSystemDto extends BaseDto {

    Long id;

    String avn;

    String contactlessAvn;

    Long paymentSystemId;

    Long groupId;

    Set<ModificationGeneralLimitDto> limits;

    @Data
    @Builder(toBuilder = true)
    @NoArgsConstructor
    @AllArgsConstructor
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class ModificationGeneralLimitDto {

        Long id;

        String name;
    }

}
