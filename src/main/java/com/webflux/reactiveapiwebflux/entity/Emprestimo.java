package com.webflux.reactiveapiwebflux.entity;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class Emprestimo {

    private Long id;

    private String cpfCnpj;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date dataDeNascimento;

    private BigDecimal valor;

    private Integer diasParaPagar;

    private Integer parcelas;
}
