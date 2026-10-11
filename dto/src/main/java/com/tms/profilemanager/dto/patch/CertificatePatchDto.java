package com.tms.profilemanager.dto.patch;

import com.tms.profilemanager.dto.CertificateDto;
import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.OffsetDateTime;
import java.util.Optional;

@Data
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CertificatePatchDto extends BasePatchDto<CertificateDto> {

    Optional<@NotBlank @Size(max = 32) String> rid;

    Optional<@NotNull Integer> index;

    Optional<@NotBlank @Size(max = 6) String> exponent;

    Optional<@NotBlank String> modulus;

    Optional<@NotBlank @Size(max = 128) String> hash;

    Optional<@NotNull OffsetDateTime> validUntil;

    Optional<@NotNull Long> paymentSystemId;
}
