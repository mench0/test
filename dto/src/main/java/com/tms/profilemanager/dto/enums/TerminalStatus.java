package com.tms.profilemanager.dto.enums;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@Getter
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public enum TerminalStatus {

    NOT_ACTIVE(0, "Not active"),
    ACTIVE(1, "Active");

    int numericCode;

    String name;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class Dto {

        @Schema(description = "Внутренний системный код статуса терминала")
        String systemCode;

        @Schema(description = "Числовой код статуса терминала")
        int numericCode;

        @Schema(description = "Название статуса терминала")
        String name;

        public static TerminalStatus.Dto of(TerminalStatus terminalStatus) {
            if (terminalStatus == null) {
                return null;
            }
            return new TerminalStatus.Dto(terminalStatus.toString(), terminalStatus.getNumericCode(), terminalStatus.getName());
        }
    }
}
