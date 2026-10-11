package com.tms.profilemanager.dto;

import io.swagger.v3.oas.annotations.media.Schema;
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
@FieldDefaults(level = AccessLevel.PROTECTED)
public class PaymentSystemDto extends BaseDto {

    @Schema(description = "Название (уникальное)")
    String name;

    String avn;

    String contactlessAvn;

    Boolean pinBypassAllowed;

    Set<GeneralLimitDto> limits;
}
