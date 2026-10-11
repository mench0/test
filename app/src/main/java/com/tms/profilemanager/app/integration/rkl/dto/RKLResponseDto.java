package com.tms.profilemanager.app.integration.rkl.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RKLResponseDto {

    Set<RKLEncryptedKeyDto> encryptedKeys;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class RKLEncryptedKeyDto {

        @JsonProperty("id")
        Long acquirerId;

        String keyScheme;

        String encryptedKey;

        @JsonProperty("kcv")
        String pinMasterKeyKCV;

        String tid;

        String mid;
    }
}
