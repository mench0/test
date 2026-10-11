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
public class MerchantResponseDto {

    Integer code;
    String message;
    DataBlock data;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class DataBlock {
        Integer id;
        String email;
        String identityNumber;
        String firstName;
        String lastName;
        String fullName;
        Integer roleId;
        Integer statusId;
        String phoneNumber;
        String dob;
        String description;
        String updatedAt;
        String createdAt;
        String emailOther;
        String merchantNameEn;
        String phoneNumberOther;
        String mId;
        String address;
        Integer merchantTypeId;
    }
}

