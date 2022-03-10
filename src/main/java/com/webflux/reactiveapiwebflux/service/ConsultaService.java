package com.webflux.reactiveapiwebflux.service;

import com.webflux.reactiveapiwebflux.entity.ConsultaCpfCnpj;
import com.webflux.reactiveapiwebflux.exception.CpfCnpjNotValidException;
import com.webflux.reactiveapiwebflux.repository.ConsultaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class ConsultaService {

    @Autowired
    private ConsultaRepository consultaRepository;

    public Mono<ConsultaCpfCnpj> getConsultaByCpfCnpj(String cpfCnpj) throws CpfCnpjNotValidException {
        return validCpfCnpj(cpfCnpj)
                .flatMap(isValid -> {
                    if (isValid) {
                        return Mono.just(consultaRepository.findByCpfCnpj(cpfCnpj));
                    } else {
                        throw new CpfCnpjNotValidException("CPF/CNPJ is not valid!");
                    }
                });
    }

    private Mono<Boolean> validCpfCnpj(String cpfCnpj) {
        if (cpfCnpj.length() == 11) return validCpf(cpfCnpj);
        else if (cpfCnpj.length() == 14) return validCnpj(cpfCnpj);
        else throw new CpfCnpjNotValidException("Must contain 11 or 14 digits!");
    }

    public Mono<Boolean> validCpf(String CPF) {
        if (fistCpfValidation(CPF)) return Mono.just(Boolean.FALSE);

        char dig10, dig11;
        int sm, i, r, num, peso;

        sm = 0;
        peso = 10;
        for (i = 0; i < 9; i++) {
            num = CPF.charAt(i) - 48;
            sm = sm + (num * peso);
            peso = peso - 1;
        }

        r = 11 - (sm % 11);
        if ((r == 10) || (r == 11))
            dig10 = '0';
        else dig10 = (char) (r + 48);

        sm = 0;
        peso = 11;
        for (i = 0; i < 10; i++) {
            num = CPF.charAt(i) - 48;
            sm = sm + (num * peso);
            peso = peso - 1;
        }

        r = 11 - (sm % 11);
        if ((r == 10) || (r == 11))
            dig11 = '0';
        else dig11 = (char) (r + 48);

        if ((dig10 == CPF.charAt(9)) && (dig11 == CPF.charAt(10))) return Mono.just(Boolean.TRUE);
        else return Mono.just(Boolean.FALSE);

    }

    public Mono<Boolean> validCnpj(String CNPJ) {
        if (fistCnpjValidation(CNPJ)) return Mono.just(Boolean.FALSE);

        char dig13, dig14;
        int sm, i, r, num, peso;

        sm = 0;
        peso = 2;
        for (i = 11; i >= 0; i--) {

            num = CNPJ.charAt(i) - 48;
            sm += (num * peso);
            peso = peso + 1;
            if (peso == 10)
                peso = 2;
        }

        r = sm % 11;
        if ((r == 0) || (r == 1))
            dig13 = '0';
        else dig13 = (char) ((11 - r) + 48);

        sm = 0;
        peso = 2;
        for (i = 12; i >= 0; i--) {
            num = CNPJ.charAt(i) - 48;
            sm = sm + (num * peso);
            peso = peso + 1;
            if (peso == 10)
                peso = 2;
        }

        r = sm % 11;
        if ((r == 0) || (r == 1))
            dig14 = '0';
        else dig14 = (char) ((11 - r) + 48);


        if ((dig13 == CNPJ.charAt(12)) && (dig14 == CNPJ.charAt(13))) return Mono.just(Boolean.TRUE);
        else return Mono.just(Boolean.FALSE);
    }

    private Boolean fistCpfValidation(String CPF) {
        return CPF.equals("00000000000") || CPF.equals("11111111111") ||
                CPF.equals("22222222222") || CPF.equals("33333333333") ||
                CPF.equals("44444444444") || CPF.equals("55555555555") ||
                CPF.equals("66666666666") || CPF.equals("77777777777") ||
                CPF.equals("88888888888") || CPF.equals("99999999999") ||
                (CPF.length() != 11);
    }

    private Boolean fistCnpjValidation(String CNPJ) {
        return CNPJ.equals("00000000000000") || CNPJ.equals("11111111111111") ||
                CNPJ.equals("22222222222222") || CNPJ.equals("33333333333333") ||
                CNPJ.equals("44444444444444") || CNPJ.equals("55555555555555") ||
                CNPJ.equals("66666666666666") || CNPJ.equals("77777777777777") ||
                CNPJ.equals("88888888888888") || CNPJ.equals("99999999999999") ||
                (CNPJ.length() != 14);
    }
}

