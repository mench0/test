package com.tms.profilemanager.app.integration.skapps.dto.terminal;

import com.fasterxml.jackson.annotation.JsonProperty;
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
public class TerminalPatchRequestDto {

    String vendor;
    String model;
    @JsonProperty("statusId")
    Integer statusId;
    String softwareVersion;
}
