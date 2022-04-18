package com.webflux.reactiveapiwebflux.entity;

import lombok.*;
import org.hibernate.Hibernate;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Date;
import java.util.Objects;

@Builder
@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "tb_contract_item", schema = "reactive_api")
public class ContractItem {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "number_duplicata")
    private Long numberDuplicata;

    @Column(name = "cash_value")
    private BigDecimal cashValue;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Column(name = "duplicata_expiration")
    private Date duplicataExpiration;

    @ManyToOne
    @JoinColumn(name = "contract_id")
    private Contract contract;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        ContractItem that = (ContractItem) o;
        return id != null && Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
