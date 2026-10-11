package com.tms.profilemanager.app.integration.rkl.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.tms.profilemanager.dto.enums.AcquirerKeyScheme;
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
public class RKLRequestDto {

    String serialNumber;

    String algorithm;

    String cypherKey;

    Set<RKLAcquirerDto> acquirers;

    String groupName;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class RKLAcquirerDto {

        @JsonProperty("id")
        Long acquirerId;

        @JsonProperty("tid")
        String isoTerminalId;

        @JsonProperty("mid")
        String isoMerchantId;

        AcquirerKeyScheme keyScheme;

        String payload;
    }
}
