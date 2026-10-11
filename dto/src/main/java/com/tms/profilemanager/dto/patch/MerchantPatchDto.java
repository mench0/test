package com.tms.profilemanager.dto.patch;

import com.tms.profilemanager.dto.MerchantDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.FieldNameConstants;
import lombok.experimental.SuperBuilder;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.util.Optional;

import static com.tms.profilemanager.dto.create.MerchantCreateDto.PHONE_NUMBER_ERROR_MESSAGE;
import static com.tms.profilemanager.dto.create.MerchantCreateDto.PHONE_NUMBER_PATTERN;


@Data
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
@FieldNameConstants
public class MerchantPatchDto extends BasePatchDto<MerchantDto> {

    @Schema(description = "Название мерчанта")
    Optional<@NotBlank @Size(max = 255) String> name;

    @Schema(description = "Юридический адрес")
    Optional<@Size(max = 255) String> address;

    @Schema(description = "Номер телефона")
    Optional<@NotBlank @Pattern(regexp = PHONE_NUMBER_PATTERN, message = PHONE_NUMBER_ERROR_MESSAGE) String> phone;

    @Schema(description = "Почта")
    Optional<@NotBlank @Email String> email;

}
