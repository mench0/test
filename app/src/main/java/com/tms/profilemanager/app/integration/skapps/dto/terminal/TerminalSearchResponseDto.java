package com.tms.profilemanager.app.integration.skapps.dto.terminal;

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
public class TerminalSearchResponseDto {
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
        List<TerminalDto> items = new ArrayList<>();
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class TerminalDto {

        Long id;
        String serialNumber;
        @JsonProperty("tId")
        String tId;
        String vendor;
        Integer statusId;
        String model;
        String softwareVersion;
    }
}
