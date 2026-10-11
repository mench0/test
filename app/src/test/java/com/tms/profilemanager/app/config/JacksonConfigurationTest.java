package com.tms.profilemanager.app.config;


import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.Test;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class JacksonConfigurationTest {

    ObjectMapper objectMapper = JacksonConfiguration.createObjectMapper();

    @Test
    @SneakyThrows
    public void deserializeOptionalTest() {
        String json = """
                { "stringOptional": "123" }
                """;
        TestDto testDto = objectMapper.readValue(json, TestDto.class);
        assertTrue(testDto.getStringOptional().isPresent());
        assertEquals("123", testDto.getStringOptional().get());

        json = """
                { "stringOptional": null }
                """;
        testDto = objectMapper.readValue(json, TestDto.class);
        assertTrue(testDto.getStringOptional().isEmpty());

        json = "{}";
        testDto = objectMapper.readValue(json, TestDto.class);
        assertNull(testDto.getStringOptional());
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class TestDto {
        Optional<String> stringOptional;
    }
}
