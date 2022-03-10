package com.webflux.reactiveapiwebflux.controller;

import com.webflux.reactiveapiwebflux.entity.ConsultaCpfCnpj;
import com.webflux.reactiveapiwebflux.exception.CpfCnpjNotValidException;
import com.webflux.reactiveapiwebflux.request.ConsultaCpfCnpjRequest;
import com.webflux.reactiveapiwebflux.service.ConsultaService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;

import java.util.function.Function;

@Component
public class ConsultaHandler {

    private final Scheduler scheduler;
    private final ConsultaService consultaService;

    public ConsultaHandler(Scheduler scheduler, ConsultaService consultaService) {
        this.scheduler = scheduler;
        this.consultaService = consultaService;
    }

    public Mono<ServerResponse> consultaByCpfCnpj(ServerRequest request) {
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

    public Mono<ServerResponse> findAllConsulta(ServerRequest request) {
        var consultas = Flux
                .defer(() -> Flux.fromIterable(consultaService.getAllConsulta())).subscribeOn(scheduler);
        return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).body(consultas, ConsultaCpfCnpj.class);
    }

    public Mono<ServerResponse> saveConsulta(ServerRequest request) {
        return request
                .bodyToMono(ConsultaCpfCnpj.class)
                .flatMap(consulta -> consultaService.saveNewConsulta(consulta))
                .publishOn(scheduler)
                .flatMap(consulta -> ServerResponse
                        .ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(consulta))
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
