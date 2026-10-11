package com.tms.profilemanager.dto.patch;

import com.tms.profilemanager.dto.BaseDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;

import static java.util.stream.Collectors.toSet;

@Data
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public abstract class BasePatchDto<D extends BaseDto> {

    @NotNull
    Long id;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class CollectionPatch<T extends Collection<?>> {

        @Valid
        @NotEmpty
        T values;

        @NotNull
        @Schema(description = "Тип обновления (патча)")
        CollectionPatchType patchType = CollectionPatchType.REPLACE;
    }

    @NoArgsConstructor
    public static class SetPatch<T> extends CollectionPatch<Set<T>> {

        public SetPatch(Set<T> values, CollectionPatchType patchType) {
            super(values, patchType);
        }

        public <R> SetPatch<R> mapEachValue(Function<T, R> function) {
            return new SetPatch<>(getValues().stream().map(function).collect(toSet()), getPatchType());
        }

        public void forEachValue(Consumer<T> consumer) {
            getValues().forEach(consumer);
        }
    }

    @NoArgsConstructor
    public static class ListPatch<T> extends CollectionPatch<List<T>> {

        public ListPatch(List<T> values, CollectionPatchType patchType) {
            super(values, patchType);
        }
    }

    public enum CollectionPatchType {
        REPLACE, ADD, REMOVE
    }
}
