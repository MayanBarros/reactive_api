package com.webflux.reactiveapiwebflux.service;

import com.webflux.reactiveapiwebflux.entity.ConsultaCpfCnpj;
import com.webflux.reactiveapiwebflux.exception.CpfCnpjNotValidException;
import com.webflux.reactiveapiwebflux.repository.ConsultaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
public class ConsultaService {

    @Autowired
    private ConsultaRepository consultaRepository;

    @Autowired
    private ValidCpfCnpjService validCpfCnpjService;

    public Mono<ConsultaCpfCnpj> getConsultaByCpfCnpj(String cpfCnpj) throws CpfCnpjNotValidException {
        return validCpfCnpjService.validCpfCnpj(cpfCnpj)
                .flatMap(isValid -> {
                    if (isValid) {
                        return Mono.just(consultaRepository.findByCpfCnpj(cpfCnpj));
                    } else {
                        throw new CpfCnpjNotValidException("CPF/CNPJ is not valid!");
                    }
                });
    }

    public Mono<ConsultaCpfCnpj> getConsultaById(Long id) {
        return Mono.just(consultaRepository.
                findById(id)
                .orElseThrow());
    }

    public List<ConsultaCpfCnpj> getAllConsulta() {
        return consultaRepository.findAll();
    }

    public Mono<ConsultaCpfCnpj> saveNewConsulta(ConsultaCpfCnpj newConsulta) {
        var cpfCnpj = newConsulta.getCpfCnpj();
        return validCpfCnpjService.validCpfCnpj(cpfCnpj)
                .flatMap(isValid -> {
                    if (isValid) {
                        var exits = consultaRepository.findByCpfCnpj(cpfCnpj);
                        if (!exits.getCpfCnpj().isBlank()) {
                            newConsulta.setId(exits.getId());
                            newConsulta.setResult(true);
                            newConsulta.setCreateDate(exits.getCreateDate());
                            Mono.just(consultaRepository.save(newConsulta));
                        }
                        return Mono.just(consultaRepository.save(newConsulta));
                    } else {
                        throw new CpfCnpjNotValidException("CPF/CNPJ is not valid!");
                    }
                });
    }

    public Mono<ConsultaCpfCnpj> updateConsulta(ConsultaCpfCnpj consulta) {
        return Mono.just(consultaRepository.save(consulta));
    }

    public Mono<Void> deleteConsultaById(String id) {
        consultaRepository.deleteById(Long.valueOf(id));
        return Mono.empty();
    }
}

