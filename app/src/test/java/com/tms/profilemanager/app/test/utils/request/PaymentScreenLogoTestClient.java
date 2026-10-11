package com.tms.profilemanager.app.test.utils.request;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tms.profilemanager.app.config.JacksonConfiguration;
import com.tms.profilemanager.app.integration.skapps.config.SKAppsProperties;
import com.tms.profilemanager.dto.PaymentScreenLogoDto;
import com.tms.profilemanager.dto.auth.AuthRequestDto;
import com.tms.profilemanager.dto.auth.AuthResponseDto;
import com.tms.profilemanager.dto.create.PaymentScreenLogoCreateDto;
import com.tms.profilemanager.dto.patch.PaymentScreenLogoPatchDto;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.stereotype.Component;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

import static org.springframework.http.HttpMethod.POST;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.request;

@Component
public class PaymentScreenLogoTestClient {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper = JacksonConfiguration.createObjectMapper();
    @Autowired
    SKAppsProperties skAppsProperties;
    @Autowired
    RequestSender requestSender;

    @SneakyThrows
    public List<PaymentScreenLogoDto> getAll(String token) {

        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders
                .get("/api/payment-screen-logos")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON);

        MvcResult result = mockMvc.perform(builder).andReturn();

        int status = result.getResponse().getStatus();
        String content = result.getResponse().getContentAsString();

        if (status >= 200 && status < 300) {
            return objectMapper.readValue(content, new TypeReference<List<PaymentScreenLogoDto>>() {});
        }

        throw new ResponseStatusException(HttpStatus.valueOf(status), content);
    }

    @SneakyThrows
    public List<PaymentScreenLogoDto> getLogosInGroup(String token, Long groupId) {

        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders
                .get("/api/payment-screen-logos/groups/" + groupId)
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON);

        MvcResult result = mockMvc.perform(builder).andReturn();

        int status = result.getResponse().getStatus();
        String content = result.getResponse().getContentAsString();

        if (status >= 200 && status < 300) {
            return objectMapper.readValue(content, new TypeReference<List<PaymentScreenLogoDto>>() {});
        }

        throw new ResponseStatusException(HttpStatus.valueOf(status), content);
    }


    @SneakyThrows
    public void upload(MockMultipartFile file, PaymentScreenLogoCreateDto createDto, String token) {

        MockMultipartFile jsonPart = new MockMultipartFile(
                "createDto",
                "createDto.json",
                MediaType.APPLICATION_JSON_VALUE,
                objectMapper.writeValueAsBytes(createDto)
        );

        MockHttpServletRequestBuilder builder = multipart("/api/payment-screen-logos")
                .file(file)
                .file(jsonPart)
                .header("Authorization", "Bearer " + token);

        MvcResult result = mockMvc.perform(builder).andReturn();

        int status = result.getResponse().getStatus();
        String content = result.getResponse().getContentAsString();

        if (status >= 400) {
            throw new ResponseStatusException(HttpStatus.valueOf(status), content);
        }
    }

    @SneakyThrows
    public void update(MockMultipartFile file, PaymentScreenLogoPatchDto paymentScreenLogoPatchDto, String token, Long id) {

        MockMultipartFile jsonPart = new MockMultipartFile(
                "patchDto",
                "patchDto.json",
                MediaType.APPLICATION_JSON_VALUE,
                objectMapper.writeValueAsBytes(paymentScreenLogoPatchDto)
        );

        MockHttpServletRequestBuilder builder = multipart("/api/payment-screen-logos/" + id)
                .file(file)
                .file(jsonPart)
                .with(request -> {
                    request.setMethod("PUT");
                    return request;
                })
                .header("Authorization", "Bearer " + token);


        MvcResult result = mockMvc.perform(builder).andReturn();

        int status = result.getResponse().getStatus();
        String content = result.getResponse().getContentAsString();

        if (status >= 400) {
            throw new ResponseStatusException(HttpStatus.valueOf(status), content);
        }
    }

    @SneakyThrows
    public void delete(String token, Long id) {
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders
                .delete("/api/payment-screen-logos/" + id)
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON);

        MvcResult result = mockMvc.perform(builder).andReturn();

        int status = result.getResponse().getStatus();
        String content = result.getResponse().getContentAsString();

        if (status >= 400) {
            throw new ResponseStatusException(HttpStatus.valueOf(status), content);
        }
    }

    public String auth(String username) throws Exception {
        MvcResult result = mockMvc.perform(request(POST, "/api/auth/login")
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new AuthRequestDto(username, "12345")))
        ).andReturn();
        HttpStatus status = HttpStatus.valueOf(result.getResponse().getStatus());
        if (!status.is2xxSuccessful()) {
            throw new ResponseStatusException(status);
        }
        String responseBody = result.getResponse().getContentAsString();
        return objectMapper.readValue(responseBody, AuthResponseDto.class)
                .getAccessToken()
                .getValue();

    }
}
