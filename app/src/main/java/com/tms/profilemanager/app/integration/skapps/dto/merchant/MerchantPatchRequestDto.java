package com.tms.profilemanager.app.integration.skapps.dto.merchant;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MerchantPatchRequestDto {
    String merchantNameEn;
    String email;
    String phoneNumber;
    String description;
    String address;
}
