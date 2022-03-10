package com.webflux.reactiveapiwebflux.controller;

import com.webflux.reactiveapiwebflux.exception.CpfCnpjNotValidException;
import com.webflux.reactiveapiwebflux.request.ConsultaCpfCnpjRequest;
import com.webflux.reactiveapiwebflux.service.ConsultaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.util.function.Function;

@Component
@RequiredArgsConstructor
public class ConsultaHandler {

    private final ConsultaService consultaService;

    public Mono<ServerResponse> consultaCpfCnpj(ServerRequest request) {
        return createObjectRequest(request)
                .flatMap(consultaRequest -> consultaService.getConsultaByCpfCnpj(consultaRequest.getCpfCnpj()))
                .flatMap(consulta -> {
                    return ServerResponse.ok().bodyValue(consulta);
                })
                .onErrorResume(CpfCnpjNotValidException.class, e -> {
                    return ServerResponse.badRequest().bodyValue(e.getMessage());
                })
                .onErrorResume(treatGenericError());
    }

    private Function<Throwable, Mono<ServerResponse>> treatGenericError() {
        return error -> {
            return ServerResponse
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .bodyValue(error.getMessage());
        };
    }

    private Mono<ConsultaCpfCnpjRequest> createObjectRequest(ServerRequest request) {
        var consultaRequest = ConsultaCpfCnpjRequest
                .builder()
                .cpfCnpj(request.pathVariable("cpfCnpj"))
                .build();
        return Mono.just(consultaRequest);
    }
}
