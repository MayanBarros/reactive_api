package com.webflux.reactiveapiwebflux.controller;

import com.webflux.reactiveapiwebflux.entity.ConsultaCpfCnpj;
import com.webflux.reactiveapiwebflux.exception.CpfCnpjNotValidException;
import com.webflux.reactiveapiwebflux.request.ConsultaCpfCnpjRequest;
import com.webflux.reactiveapiwebflux.service.ConsultaService;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;

import java.util.HashMap;
import java.util.Map;
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
        // set model attributes
        Map<String, Object> model = new HashMap<>();
        model.put("consultas", consultas);

        return ServerResponse
                .status(HttpStatus.OK)
                .contentType(MediaType.TEXT_HTML)
                .render("index", model);
    }

    public Mono<ServerResponse> saveConsulta(ServerRequest request) {
        var reqBodyMono = request.bodyToMono(ConsultaCpfCnpj.class);
        return reqBodyMono
                .flatMap(consultaService::saveNewConsulta)
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

    public Mono<ServerResponse> updateConsulta(ServerRequest request) {
        var consultaId = request.pathVariable("id");
        var existingConsulta = consultaService.getConsultaById(Long.parseLong(consultaId));
        return existingConsulta
                .flatMap(consulta -> request.bodyToMono(ConsultaCpfCnpj.class)
                        .map(reqConsulta -> {
                            consulta.setCpfCnpj(reqConsulta.getCpfCnpj());
                            consulta.setResult(Boolean.TRUE);
                            return consulta;
                        })
                        .flatMap(consultaService::updateConsulta)
                        .flatMap(savedConsulta -> ServerResponse.ok().bodyValue(savedConsulta)))
                .onErrorResume(DataIntegrityViolationException.class, e -> {
                    return existingConsulta
                            .flatMap(c -> consultaService.getConsultaByCpfCnpj(c.getCpfCnpj()))
                            .flatMap(consultaService::updateConsulta)
                            .flatMap(c -> ServerResponse.ok().bodyValue(c));

                });
    }

    public Mono<ServerResponse> consultaById(ServerRequest request) {
        var id = request.pathVariable("id");
        return consultaService.getConsultaById(Long.parseLong(id))
                .flatMap(consulta -> ServerResponse
                        .ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(consulta))
                .onErrorResume(treatGenericError());
    }

    public Mono<ServerResponse> deleteConsultaById(ServerRequest request) {
        var consultaId = request.pathVariable("id");
        var existingConsulta = consultaService.getConsultaById(Long.parseLong(consultaId));
        return existingConsulta
                .flatMap(consulta -> consultaService.deleteConsultaById(consultaId))
                .then(ServerResponse.status(HttpStatus.NO_CONTENT).render("redirect:/consulta"));
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
