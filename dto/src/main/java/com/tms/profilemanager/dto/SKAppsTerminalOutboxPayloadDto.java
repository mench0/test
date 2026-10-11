package com.tms.profilemanager.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SKAppsTerminalOutboxPayloadDto {
    @JsonProperty("mId")
    String mId;
    @JsonProperty("tId")
    String tId;
    String serialNumber;
    String vendor;
    String model;
    Integer statusId;
    String softwareVersion;
}
