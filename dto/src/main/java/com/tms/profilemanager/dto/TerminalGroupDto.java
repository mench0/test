package com.tms.profilemanager.dto;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.List;

@Data
@SuperBuilder
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TerminalGroupDto extends BaseDto {

    @Schema(description = "Название группы терминалов")
    String name;

    int terminalsCount;

    @Schema(description = "Подгруппы")
    List<TerminalGroupDto> children;

    @Builder.Default
    @ArraySchema(arraySchema = @Schema(description = "Список логотипов"))
    List<PaymentScreenLogoDto> paymentScreenLogos = new ArrayList<>();

    public void addChild(TerminalGroupDto childGroupDto) {
        if (children == null) {
            children = new ArrayList<>();
        }
        children.add(childGroupDto);
    }
}
