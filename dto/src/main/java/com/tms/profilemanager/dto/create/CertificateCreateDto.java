package com.tms.profilemanager.dto.create;

import com.tms.profilemanager.dto.CertificateDto;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.OffsetDateTime;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CertificateCreateDto implements BaseCreateDto<CertificateDto> {

    @NotBlank
    @Size(max = 32)
    String rid;

    @NotNull
    Integer index;

    @NotBlank
    @Size(max = 6)
    String exponent;

    @NotBlank
    String modulus;

    @NotBlank
    @Size(max = 128)
    String hash;

    @NotNull
    OffsetDateTime validUntil;

    @NotNull
    Long paymentSystemId;
}
