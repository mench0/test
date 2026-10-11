package com.tms.profilemanager.app.integration.skapps.dto.merchant;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MerchantSearchResponseDto {
    DataBlock data;
    int currentPage;
    int pageSize;
    int totalCount;
    int totalPages;
    boolean hasNextPage;
    boolean hasPreviousPage;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class DataBlock {
        List<MerchantDto> items = new ArrayList<>();
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class MerchantDto {
        Long id;
        String email;
        String identityNumber;
        String phoneNumber;
        String merchantNameEn;
        @JsonProperty("mId")
        String mId;
        String address;
    }
}
