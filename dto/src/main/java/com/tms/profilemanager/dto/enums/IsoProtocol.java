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
public enum IsoProtocol {
    SMART_VISTA("SmartVista"),
    SMART_VISTA_UZCARD("SmartVista Uzcard"),
    TIETO_HUMO("Tieto Humo"),
    OPENWAY("OpenWay"),
    COMPASS_PLUS ("Compass Plus");

    String name;

    @Data
    @Schema(name = "IsoProtocolDto")
    @NoArgsConstructor
    @AllArgsConstructor
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class Dto {

        @Schema(description = "Внутренний системный код ISO протокола")
        String systemCode;

        @Schema(description = "Название ISO протокола")
        String name;

        public static Dto of(IsoProtocol protocol) {
            if (protocol == null) {
                return null;
            }
            return new Dto(protocol.toString(), protocol.getName());
        }
    }
}
