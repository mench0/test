package com.tms.profilemanager.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.FieldNameConstants;

import java.time.OffsetDateTime;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PROTECTED)
public class TerminalExportDto {

    OffsetDateTime createdAt;

    OffsetDateTime lastUpdatedAt;

    String serialNumber;

    Set<TerminalAcquirerParamsExportDto> terminalAcquirerParams;

    String merchantName;

    Long groupId;

    String configUpdatedDate;

    String version;

    String software;

    String mode;

    String vendor;

    String model;

    @Data
    @Builder(toBuilder = true)
    @NoArgsConstructor
    @AllArgsConstructor
    @FieldDefaults(level = AccessLevel.PRIVATE)
    @FieldNameConstants
    public static class TerminalAcquirerParamsExportDto {

        String isoTerminalId;

        String isoMerchantId;

        String acquirerName;

        String pinMasterKeyKCV;

        String kcvUpdatedDate;
    }
}
