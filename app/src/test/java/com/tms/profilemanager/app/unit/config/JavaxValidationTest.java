package com.tms.profilemanager.app.unit.config;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class JavaxValidationTest {

    private static ValidatorFactory validatorFactory;
    private static Validator validator;

    @BeforeAll
    public static void createValidator() {
        validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.getValidator();
    }

    @AfterAll
    public static void close() {
        validatorFactory.close();
    }

    @Test
    public void validateOptionalTest() {
        TestDto testDto = new TestDto();

        testDto.setStringOptional(Optional.of("1234567"));
        assertTrue(validator.validate(testDto).isEmpty());

        testDto.setStringOptional(null);
        assertTrue(validator.validate(testDto).isEmpty());

        testDto.setStringOptional(Optional.empty());
        assertFalse(validator.validate(testDto).isEmpty());

        testDto.setStringOptional(Optional.of("123"));
        assertFalse(validator.validate(testDto).isEmpty());

        testDto.setStringOptional(Optional.of("123456789012345"));
        assertFalse(validator.validate(testDto).isEmpty());

        testDto.setStringOptional(null);
        testDto.setIntegerOptional(Optional.empty());
        assertFalse(validator.validate(testDto).isEmpty());
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class TestDto {
        Optional<@NotBlank @Size(min = 5, max = 10) String> stringOptional;
        Optional<@NotNull Integer> integerOptional;
    }
}
