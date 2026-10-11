package com.tms.profilemanager.app.test.utils.request;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tms.profilemanager.dto.auth.AuthRequestDto;
import com.tms.profilemanager.dto.auth.AuthResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.web.server.ResponseStatusException;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpMethod.POST;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.request;

@Slf4j
@Component
@RequiredArgsConstructor
public class RequestSender {

    private final MockMvc mockMvc;
    private final ObjectMapper objectMapper;

    private String accessToken;

    public static final String DEFAULT_TEST_PASSWORD = "12345";

    @SneakyThrows
    public MvcResult send(HttpMethod method, String url, Object body) {
        MockHttpServletRequestBuilder requestBuilder = request(method, url)
                .contentType(APPLICATION_JSON);
        if (body != null) {
            if (body instanceof String) {
                requestBuilder.content(body.toString());
            } else {
                requestBuilder.content(objectMapper.writeValueAsString(body));
            }
        }
        setAuthorizationHeader(requestBuilder);
        return mockMvc.perform(requestBuilder).andReturn();
    }

    @SneakyThrows
    public MvcResult sendAndExpectOk(HttpMethod method, String url, Object body) {
        MvcResult mvcResult = send(method, url, body);
        HttpStatus status = HttpStatus.valueOf(mvcResult.getResponse().getStatus());
        if (!status.is2xxSuccessful()) {
            log.error("Request {} {} failed with code {}. Request body: {}",
                    method, url, status, mvcResult.getRequest().getContentAsString());
            throw new ResponseStatusException(status);
        }
        return mvcResult;
    }

    @SneakyThrows
    @SuppressWarnings("unchecked")
    public <T> T sendAndExpectOk(HttpMethod method, String url, Object body, Class<T> resultClass) {
        MvcResult mvcResult = sendAndExpectOk(method, url, body);
        mvcResult.getResponse().setDefaultCharacterEncoding("UTF-8");
        if (String.class.equals(resultClass)) {
            return (T) mvcResult.getResponse().getContentAsString();
        }
        return objectMapper.readValue(mvcResult.getResponse().getContentAsString(), resultClass);
    }

    @SneakyThrows
    public <T> T sendAndExpectOk(HttpMethod method, String url, Object body, TypeReference<T> resultTypeRef) {
        MvcResult mvcResult = sendAndExpectOk(method, url, body);
        return objectMapper.readValue(mvcResult.getResponse().getContentAsString(), resultTypeRef);
    }

    @SneakyThrows
    private void setAuthorizationHeader(MockHttpServletRequestBuilder requestBuilder) {
        if (accessToken != null) {
            requestBuilder.header(AUTHORIZATION, "Bearer " + accessToken);
        }
    }

    public void setCurrentUser(String username) {
        setCurrentUser(username, DEFAULT_TEST_PASSWORD);
    }

    @SneakyThrows
    public void setCurrentUser(String username, String password) {
        MvcResult result = mockMvc.perform(request(POST, "/api/auth/login")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new AuthRequestDto(username, password)))
                ).andReturn();
        HttpStatus status = HttpStatus.valueOf(result.getResponse().getStatus());
        if (!status.is2xxSuccessful()) {
            log.error("Login request failed with code {}", status);
            throw new ResponseStatusException(status);
        }
        String responseBody = result.getResponse().getContentAsString();
        accessToken = objectMapper.readValue(responseBody, AuthResponseDto.class)
                .getAccessToken()
                .getValue();
    }

    public void clearCurrentUser() {
        this.accessToken = null;
    }
}
