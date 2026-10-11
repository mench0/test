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
public enum TerminalType {
    ATTENDENT_11(11, "Attended, Online only, Financial Institution"),
    ATTENDENT_12(12, "Attended, Offline with online capabilities, Financial Institution"),
    ATTENDENT_13(13, "Attended, Offline only, Financial Institution"),
    ATTENDENT_21(21, "Attended, Online only, Merchant"),
    ATTENDENT_22(22, "Attended, Offline with online capabilities, Merchant"),
    ATTENDENT_23(23, "Attended, Offline only, Merchant"),
    UNATTENDENT_14(14, "Unattended, Online only, Financial Institution"),
    UNATTENDENT_15(15, "Unattended, Offline with online capabilities, Financial Institution"),
    UNATTENDENT_16(16, "Unattended, Offline only, Financial Institution"),
    UNATTENDENT_24(24, "Unattended, Online only, Merchant"),
    UNATTENDENT_25(25, "Unattended, Offline with online capabilities, Merchant"),
    UNATTENDENT_26(26, "Unattended, Offline only, Merchant"),
    UNATTENDENT_34(34, "Unattended, Online only, Cardholder"),
    UNATTENDENT_35(35, "Unattended, Offline with online capabilities, Cardholder"),
    UNATTENDENT_36(36, "Unattended, Offline only, Cardholder");

    int numericCode;

    String name;

    @Data
    @Schema(name = "TerminalTypeDto")
    @NoArgsConstructor
    @AllArgsConstructor
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class Dto {

        @Schema(description = "Внутренний системный код типа терминала")
        String systemCode;

        @Schema(description = "Числовой код типа терминала")
        int numericCode;

        @Schema(description = "Название типа терминала")
        String name;

        public static Dto of(TerminalType terminalType) {
            if (terminalType == null) {
                return null;
            }
            return new Dto(terminalType.toString(), terminalType.getNumericCode(), terminalType.getName());
        }
    }
}
