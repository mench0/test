package com.tms.profilemanager.app.unit.config;


import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tms.profilemanager.app.config.JacksonConfiguration;
import com.tms.profilemanager.dto.BaseDto;
import com.tms.profilemanager.dto.view.JsonViews;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JacksonConfigurationTest {

    ObjectMapper objectMapper = JacksonConfiguration.createObjectMapper();

    @Test
    @SneakyThrows
    void deserializeOptionalTest() {
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

    @Test
    @SneakyThrows
    void defaultJsonViewTest() {
        TestDto testDto = TestDto.builder()
                .id(1L)
                .createdAt(OffsetDateTime.now())
                .archived(false)
                .stringOptional(Optional.of("test"))
                .extendedString("extended")
                .build();

        String json = objectMapper.writeValueAsString(testDto);
        JsonNode jsonNode = objectMapper.readTree(json);
        assertNotNull(jsonNode.get("createdAt"));
        assertNotNull(jsonNode.get("archived"));
        assertNotNull(jsonNode.get("extendedString"));

        json = objectMapper.writerWithView(JsonViews.Default.class).writeValueAsString(testDto);
        jsonNode = objectMapper.readTree(json);
        assertNotNull(jsonNode.get("createdAt"));
        assertNotNull(jsonNode.get("archived"));
        assertNull(jsonNode.get("extendedString"));

        json = objectMapper.writerWithView(JsonViews.Extended.class).writeValueAsString(testDto);
        jsonNode = objectMapper.readTree(json);
        assertNull(jsonNode.get("createdAt"));
        assertNull(jsonNode.get("archived"));
        assertNotNull(jsonNode.get("extendedString"));
    }

    @Data
    @SuperBuilder
    @NoArgsConstructor
    @AllArgsConstructor
    @EqualsAndHashCode(callSuper = true)
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class TestDto extends BaseDto {

        Optional<String> stringOptional;

        @JsonView(JsonViews.Extended.class)
        String extendedString;
    }
}
