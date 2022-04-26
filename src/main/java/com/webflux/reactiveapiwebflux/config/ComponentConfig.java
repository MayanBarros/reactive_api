package com.webflux.reactiveapiwebflux.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import javax.transaction.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.math.BigDecimal;

@Configuration
public class ComponentConfig {

    private final String query = "SELECT vl_parameter FROM tb_parametro WHERE description='TAX_PATTERN'";

    @Value("${tax.pattern.annual}")
    private BigDecimal taxPatternAnnualYML;

    @PersistenceContext
    private EntityManager entityManager;

    @Transactional
    public BigDecimal execute(String sql) {
        return (BigDecimal) entityManager.createNativeQuery(sql).getSingleResult();
    }

    @Bean
    public BigDecimal taxaAnualMenor() {
            BigDecimal taxPatternAnnualDB = execute(query);
            BigDecimal TAX_PATTERN_FINAL;
        return TAX_PATTERN_FINAL = taxPatternAnnualDB.compareTo(this.taxPatternAnnualYML) == -1  ? taxPatternAnnualDB : this.taxPatternAnnualYML;

    }
}
