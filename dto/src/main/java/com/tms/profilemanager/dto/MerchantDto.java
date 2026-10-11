package com.tms.profilemanager.dto;

import io.swagger.v3.oas.annotations.media.Schema;
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
public class MerchantDto extends BaseDto {

    @Schema(description = "Название мерчанта")
    String name;

    @Schema(description = "Юридический адрес")
    String address;

    @Schema(description = "Номер телефона")
    String phone;

    @Schema(description = "Почта")
    String email;

    @Schema(description = "ИНН или ПИНФЛ")
    String innOrPinfl;
}
