package com.tms.profilemanager.dto.create;

import com.tms.profilemanager.dto.MerchantDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.FieldNameConstants;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;


@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@FieldNameConstants
public class MerchantCreateDto implements BaseCreateDto<MerchantDto> {


    public static final String PHONE_NUMBER_PATTERN = "^[+]\\d+$";
    public static final String PHONE_NUMBER_ERROR_MESSAGE = "{validation.merchant.phone.pattern}";

    @NotBlank
    @Size(max = 255)
    @Schema(description = "Название мерчанта")
    String name;

    @Schema(description = "Юридический адрес")
    @Size(max = 255)
    String address;

    @NotBlank
    @Pattern(regexp = PHONE_NUMBER_PATTERN, message = PHONE_NUMBER_ERROR_MESSAGE)
    @Schema(description = "Номер телефона")
    String phone;

    @NotBlank
    @Email
    @Schema(description = "Почта")
    String email;

    @NotBlank
    @Size(min = 7, max = 14)
    @Schema(description = "ИНН или ПИНФЛ")
    String innOrPinfl;

}
