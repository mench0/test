package com.tms.profilemanager.app.integration.skapps.dto.terminal;

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
public class TerminalCreateRequestDto {

    @JsonProperty("mId")
    String mId;
    @JsonProperty("tId")
    String tId;
    String serialNumber;
    String vendor;
    String model;
    @JsonProperty("statusId")
    Integer statusId;
    String softwareVersion;
}
