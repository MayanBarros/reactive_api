package com.webflux.reactiveapiwebflux.controller;

import com.webflux.reactiveapiwebflux.entity.Contract;
import com.webflux.reactiveapiwebflux.entity.ContractItem;
import com.webflux.reactiveapiwebflux.exception.CpfCnpjNotValidException;
import com.webflux.reactiveapiwebflux.request.ContractRequest;
import com.webflux.reactiveapiwebflux.service.ContractService;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

@Component
public class ContractHandler {

    private final Scheduler scheduler;
    private final ContractService contractService;

    public ContractHandler(Scheduler scheduler, ContractService contractService) {
        this.scheduler = scheduler;
        this.contractService = contractService;
    }

    public Mono<ServerResponse> contractByCpfCnpj(ServerRequest request) {
        Map<String, Object> model = new HashMap<>();
        return createObjectRequest(request)
                .flatMap(contractRequest -> contractService.getContractByCpfCnpj(contractRequest.getCpfCnpj()))
                .flatMap(contract -> {
                    model.put("contracts", contract);
                    return ServerResponse
                            .status(HttpStatus.OK)
                            .contentType(MediaType.TEXT_HTML)
                            .render("list", model);
                })
                .onErrorResume(CpfCnpjNotValidException.class, e -> {
                    model.put("message", e.getMessage());
                    return ServerResponse
                            .status(HttpStatus.BAD_REQUEST)
                            .contentType(MediaType.TEXT_HTML)
                            .render("list", model);
                })
                .onErrorResume(treatGenericError());
    }

    public Mono<ServerResponse> findAllContract(ServerRequest request) {
        var contracts = Flux
                .defer(() -> Flux.fromIterable(contractService.getAllContract())).subscribeOn(scheduler);
        Map<String, Object> model = new HashMap<>();
        model.put("contracts", contracts);
        return ServerResponse
                .status(HttpStatus.OK)
                .contentType(MediaType.TEXT_HTML)
                .render("list", model);
    }

//    public Mono<ServerResponse> saveContract(ServerRequest request) {
//        Map<String, Object> model = new HashMap<>();
//        var reqBodyMono = request.formData();
//        return reqBodyMono.map(this::formDataToContract)
//                .flatMap(contractService::saveNewContract)
//                .publishOn(scheduler)
//                .flatMap(contract -> {
//                    return ServerResponse.status(HttpStatus.CREATED).contentType(MediaType.TEXT_HTML).render("redirect:/contract");
//                })
//                .onErrorResume(CpfCnpjNotValidException.class, e -> {
//                    model.put("message", e.getMessage());
//                    model.put("contract", new Contract());
//                    return ServerResponse.status(HttpStatus.BAD_REQUEST).render("form", model);
//                })
//                .onErrorResume(treatGenericError());
//    }

    public Mono<ServerResponse> newContract(ServerRequest request) {
        Map<String, Object> model = new HashMap<>();
        Contract contract = new Contract();
        model.put("contract", contract);
        return ServerResponse.ok().contentType(MediaType.TEXT_HTML).render("form", model);
    }

    public Mono<ServerResponse> updateContract(ServerRequest request) {
        var contractId = request.pathVariable("id");
        var existingContract = contractService.getContractById(Long.parseLong(contractId));
        return existingContract
                .flatMap(contract -> request.bodyToMono(Contract.class)
                        .map(reqContract -> {
                            contract.setCpfCnpj(reqContract.getCpfCnpj());
                            contract.setResult(Boolean.TRUE);
                            return contract;
                        })
                        .flatMap(contractService::updateContract)
                        .flatMap(savedContract -> ServerResponse.ok().bodyValue(savedContract)))
                .onErrorResume(DataIntegrityViolationException.class, e -> {
                    return existingContract
                            .flatMap(c -> contractService.getContractByCpfCnpj(c.getCpfCnpj()))
                            .flatMap(contractService::updateContract)
                            .flatMap(c -> ServerResponse.ok().bodyValue(c));
                });
    }

    public Mono<ServerResponse> editContract(ServerRequest request) {
        Map<String, Object> model = new HashMap<>();
        var contractId = request.pathVariable("id");
        var existingContract = contractService.getContractById(Long.parseLong(contractId));
        return existingContract
                .flatMap(savedContract -> {
                    model.put("contract", savedContract);
                    return ServerResponse.status(HttpStatus.CREATED).render("form", model);
                    });
//                .onErrorResume(DataIntegrityViolationException.class, e -> {
//                    return existingContract
//                            .flatMap(c -> contractService.getContractByCpfCnpj(c.getCpfCnpj()))
//                            .flatMap(contractService::updateContract)
//                            .flatMap(c -> {
//                                model.put("contract", c);
//                                return ServerResponse.status(HttpStatus.CREATED).render("form", model);
//                            });
//                });
    }

    public Mono<ServerResponse> contractById(ServerRequest request) {
        var id = request.pathVariable("id");
        Map<String, Object> model = new HashMap<>();
        return contractService.getContractById(Long.parseLong(id))
                .flatMap(contract -> {
                    model.put("contracts", contract);
                    return ServerResponse
                        .ok()
                        .contentType(MediaType.TEXT_HTML)
                        .render("list", model);
                })
                .onErrorResume(treatGenericError());
    }

    public Mono<ServerResponse> deleteContractById(ServerRequest request) {
        var contractId = request.pathVariable("id");
        var existingContract = contractService.getContractById(Long.parseLong(contractId));
        return existingContract
                .flatMap(contractService::deleteContract)
                .then(ServerResponse.status(HttpStatus.NO_CONTENT).render("redirect:/contract"));
    }

    private Function<Throwable, Mono<ServerResponse>> treatGenericError() {
        return error -> {
            Map<String, Object> model = new HashMap<>();
            model.put("message", error.getMessage());
            model.put("contract", new Contract());
            return ServerResponse
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .contentType(MediaType.TEXT_HTML)
                    .render("form", model);
        };
    }

    private Mono<ContractRequest> createObjectRequest(ServerRequest request) {
        var contractRequest = ContractRequest
                .builder()
                .cpfCnpj(request.pathVariable("cpfCnpj"))
                .build();
        return Mono.just(contractRequest);
    }

    private Contract formDataToContract(MultiValueMap<String, String> formData) {
        Contract contract = new Contract();
        var map = formData.toSingleValueMap();
        contract.setCpfCnpj(map.get("cpfCnpj"));
        //contract.setContractItem(map.get("contractItem"));
        return contract;
    }
}
