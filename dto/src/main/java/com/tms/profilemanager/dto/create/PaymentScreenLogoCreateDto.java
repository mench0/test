package com.tms.profilemanager.dto.create;

import com.tms.profilemanager.dto.PaymentScreenLogoDto;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import javax.validation.constraints.NotBlank;


@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PaymentScreenLogoCreateDto implements BaseCreateDto<PaymentScreenLogoDto> {

    @NotBlank
    String name;

}
