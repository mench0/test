package com.tms.profilemanager.dto.enums;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@Getter
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public enum AcquirerKeyScheme {
    SMARTVISTA_MPS("smartvista_mps"),
    SMARTVISTA_LOCAL("smartvista_local"),
    HUMO("humo"),
    NONE("none");

    @JsonValue
    String code;
}
