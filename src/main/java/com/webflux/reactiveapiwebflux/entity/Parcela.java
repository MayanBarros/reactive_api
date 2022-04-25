package com.webflux.reactiveapiwebflux.entity;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class Parcela {

    //@Id
    //@GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int mes;

    private LocalDateTime data;

    private BigDecimal juros;

    //@Column(name = "prestacao_mensal")
    private BigDecimal prestacao;

    //@Column(name = "saldo_devedor")
    private BigDecimal saldoDevedor;

    private BigDecimal amortizacao;

    private Integer emprestimo_id;

    public Parcela(Long id, int mes, LocalDateTime data, BigDecimal totalParcela) {
        this.id = id;
        this.mes = mes;
        this.data = data;
        this.prestacao = totalParcela;
    }

    public Parcela(Long id, int mes, LocalDateTime data, BigDecimal juros, BigDecimal totalParcela, BigDecimal saldoDevedor, BigDecimal amortizacao) {
        this.id = id;
        this.mes = mes;
        this.data = data;
        this.juros = juros;
        this.prestacao = totalParcela;
        this.saldoDevedor = saldoDevedor;
        this.amortizacao = amortizacao;
    }

}
