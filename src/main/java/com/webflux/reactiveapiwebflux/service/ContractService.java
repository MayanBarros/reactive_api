package com.webflux.reactiveapiwebflux.service;

import com.webflux.reactiveapiwebflux.entity.Contract;
import com.webflux.reactiveapiwebflux.exception.CpfCnpjNotValidException;
import com.webflux.reactiveapiwebflux.repository.ContractRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
public class ContractService {

    @Autowired
    private ContractRepository contractRepository;

    @Autowired
    private ValidCpfCnpjService validCpfCnpjService;

    public Mono<Contract> getContractByCpfCnpj(String cpfCnpj) throws CpfCnpjNotValidException {
        return Mono.just(validCpfCnpjService.validCpfCnpj(cpfCnpj))
                .flatMap(isValid -> {
                    if (isValid) {
                        return Mono.just(contractRepository.findByCpfCnpj(cpfCnpj));
                    } else {
                        throw new CpfCnpjNotValidException("CPF/CNPJ is not valid!");
                    }
                });
    }

    public Mono<Contract> getContractById(Long id) {
        return Mono.just(contractRepository.
                findById(id)
                .orElseThrow());
    }

    public Contract getEditContractById(Long id) {
        return contractRepository.
                findById(id)
                .orElseThrow();
    }

    public List<Contract> getAllContract() {
        return contractRepository.findAll();
    }

    public Contract saveNewContract(Contract newContract) {
        var cpfCnpj = newContract.getCpfCnpj();
        if (validCpfCnpjService.validCpfCnpj(cpfCnpj)) {
            var exits = contractRepository.findByCpfCnpj(cpfCnpj);
            if (exits != null) {
                newContract.setId(exits.getId());
                newContract.setResult(true);
                newContract.setCreateDate(exits.getCreateDate());
                newContract.setContractExpiration(exits.getContractExpiration());
                return contractRepository.save(newContract);
            }
            newContract.setResult(true);
            return contractRepository.save(newContract);
        } else {
            throw new CpfCnpjNotValidException("CPF/CNPJ is not valid!");
        }
    }

    public Contract editContract(Contract newContract) {
        var cpfCnpj = newContract.getCpfCnpj();
        if (validCpfCnpjService.validCpfCnpj(cpfCnpj)) {
                newContract.setResult(true);
                return contractRepository.save(newContract);
        } else {
            throw new CpfCnpjNotValidException("CPF/CNPJ is not valid!");
        }
    }

//    public Mono<Contract> saveNewContract(Contract newContract) {
//        var cpfCnpj = newContract.getCpfCnpj();
//        return validCpfCnpjService.validCpfCnpj(cpfCnpj)
//                .flatMap(isValid -> {
//                    if (isValid) {
//                        var exits = contractRepository.findByCpfCnpj(cpfCnpj);
//                        if (exits != null) {
//                            newContract.setId(exits.getId());
//                            newContract.setResult(true);
//                            newContract.setCreateDate(exits.getCreateDate());
//                            Mono.just(contractRepository.save(newContract));
//                        }
//                        newContract.setResult(true);
//                        return Mono.just(contractRepository.save(newContract));
//                    } else {
//                        throw new CpfCnpjNotValidException("CPF/CNPJ is not valid!");
//                    }
//                });
//    }

    public Mono<Contract> updateContract(Contract contract) {
        return Mono.just(contractRepository.save(contract));
    }

    public Mono<Void> deleteContractById(String id) {
        contractRepository.deleteById(Long.valueOf(id));
        return Mono.empty();
    }

    public Mono<Void> deleteContract(Contract contract) {
        contractRepository.delete(contract);
        return Mono.empty();
    }
}

