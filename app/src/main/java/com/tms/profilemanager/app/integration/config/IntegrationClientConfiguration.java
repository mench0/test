package com.tms.profilemanager.app.integration.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tms.profilemanager.dto.error.ErrorResponseDto;
import io.netty.channel.ChannelOption;
import io.netty.resolver.DefaultAddressResolverGroup;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.http.codec.json.Jackson2JsonDecoder;
import org.springframework.http.codec.json.Jackson2JsonEncoder;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ResponseStatusException;
import org.zalando.logbook.Logbook;
import org.zalando.logbook.netty.LogbookClientHandler;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;

import java.util.function.Function;

import static org.springframework.http.MediaType.APPLICATION_JSON;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class IntegrationClientConfiguration {

    private static final String REQUEST_ERROR_CODE = "request_error";

    private final ObjectMapper objectMapper;
    private final IntegrationProperties integrationProperties;

    @Bean
    public HttpClient httpClient(Logbook logbookExternalClient) {
        return HttpClient.create()
                .resolver(DefaultAddressResolverGroup.INSTANCE)
                .responseTimeout(integrationProperties.getResponseTimeout())
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, (int) integrationProperties.getConnectionTimeout().toMillis())
                .doOnConnected(connection -> connection.addHandlerLast(new LogbookClientHandler(logbookExternalClient)));
    }

    @Bean
    @Qualifier("rklWebClient")
    public WebClient rklWebClient(HttpClient httpClient) {
        return WebClient.builder()
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .filter(ExchangeFilterFunction.ofResponseProcessor(errorHandlingFunction()))
                .codecs(configurer -> {
                    configurer.defaultCodecs().jackson2JsonEncoder(new Jackson2JsonEncoder(objectMapper, APPLICATION_JSON));
                    configurer.defaultCodecs().jackson2JsonDecoder(new Jackson2JsonDecoder(objectMapper, APPLICATION_JSON));
                })
                .build();
    }

    @Bean
    @Qualifier("skappsWebClient")
    public WebClient skappsWebClient(HttpClient httpClient) {
        return WebClient.builder()
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .codecs(configurer -> {
                    configurer.defaultCodecs().jackson2JsonEncoder(new Jackson2JsonEncoder(objectMapper, APPLICATION_JSON));
                    configurer.defaultCodecs().jackson2JsonDecoder(new Jackson2JsonDecoder(objectMapper, APPLICATION_JSON));
                })
                .build();
    }

    private Function<ClientResponse, Mono<ClientResponse>> errorHandlingFunction() {
        return response -> {
            HttpStatus status = response.statusCode();
            if (status.is2xxSuccessful()) {
                return Mono.just(response);
            }
            return response.bodyToMono(String.class)
                    .flatMap(body -> {
                        try {
                            ErrorResponseDto responseDto = ErrorResponseDto.builder()
                                    .errorCode(REQUEST_ERROR_CODE)
                                    .errorMessage(body)
                                    .build();
                            body = objectMapper.writeValueAsString(responseDto);
                        } catch (Exception e) {
                            log.error("Error during serialization ErrorResponseDto: ", e);
                        }
                        return Mono.error(new ResponseStatusException(status, body));
                    });
        };
    }

}
