package com.tms.profilemanager.app.integration.skapps.dto.merchant;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MerchantCreateRequestDto {
    @JsonProperty("mId")
    String mId;
    String merchantNameEn;
    String email;
    String phoneNumber;
    String identityNumber;
    String description;
    String address;
}
