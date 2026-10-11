package com.tms.profilemanager.dto.request;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.FieldNameConstants;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.util.Set;

import static com.tms.profilemanager.dto.create.TerminalCreateDto.TerminalAcquirerParamsInputDto.MID_AND_TID_PATTERN;
import static com.tms.profilemanager.dto.create.TerminalCreateDto.TerminalAcquirerParamsInputDto.MID_PATTERN_ERROR_CODE;
import static com.tms.profilemanager.dto.create.TerminalCreateDto.TerminalAcquirerParamsInputDto.TID_PATTERN_ERROR_CODE;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@FieldNameConstants
public class TerminalEncryptedKeysRequestV2Dto {

    @NotBlank
    String algorithm;

    @NotBlank
    String cypherKey;

    @NotEmpty
    Set<@NotNull AcquirerTerminalKey> acquirers;

    @Data
    @Builder(toBuilder = true)
    @NoArgsConstructor
    @AllArgsConstructor
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class AcquirerTerminalKey {

        @NotNull
        Long acquirerId;

        @NotNull
        @Size(min = 1, max = 8)
        @Pattern(regexp = MID_AND_TID_PATTERN, message = TID_PATTERN_ERROR_CODE)
        String tid;

        @NotNull
        @Size(min = 1, max = 15)
        @Pattern(regexp = MID_AND_TID_PATTERN, message = MID_PATTERN_ERROR_CODE)
        String mid;

    }

}
