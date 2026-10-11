package com.tms.profilemanager.dto;

import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SKAppsMerchantOutboxPayloadDto {
    String mId;
    String merchantNameEn;
    String email;
    String phoneNumber;
    String identityNumber;
    String description;
    String address;
}
