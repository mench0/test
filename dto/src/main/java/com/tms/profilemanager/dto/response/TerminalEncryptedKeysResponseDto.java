package com.tms.profilemanager.dto.response;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.FieldNameConstants;

import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@FieldNameConstants
public class TerminalEncryptedKeysResponseDto {

    Set<TerminalEncryptedKeyDto> encryptedKeys;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class TerminalEncryptedKeyDto {

        Long acquirerId;

        String keyScheme;

        String encryptedKey;

        String pinMasterKeyKCV;

        String tid;

        String mid;

    }
}
