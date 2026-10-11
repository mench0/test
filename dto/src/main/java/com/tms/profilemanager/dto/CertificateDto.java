package com.tms.profilemanager.dto;

import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;

import java.time.OffsetDateTime;

@Data
@SuperBuilder
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CertificateDto extends BaseDto {

    String rid;

    Integer index;

    String exponent;

    String modulus;

    String hash;

    OffsetDateTime validUntil;

    Long paymentSystemId;
}
