package com.tms.profilemanager.app.integration.skapps.dto.terminal;

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
public class TerminalResponseDto {

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
        String serialNumber;
        String tId;
        String vendor;
        Integer statusId;
        String softwareVersion;
        Integer merchantId;
        String createdAt;
        String updatedAt;
        Integer transactionCount;
        Status status;
        String model;
        String lastTransactionDate;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class Status {
        Integer id;
        String hy;
        String en;
        String ru;
        String uz;
        String updatedAt;
        String createdAt;
        Boolean isActive;
        String deletedAt;
    }

}
