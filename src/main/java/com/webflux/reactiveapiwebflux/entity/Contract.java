package com.webflux.reactiveapiwebflux.entity;

import lombok.*;
import org.hibernate.Hibernate;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
@Entity
@Table(name = "tb_contract", schema = "reactive_api")
public class Contract {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "CPF/CNPJ cannot be blank!")
    @Column(name = "cpf_cnpj")
    private String cpfCnpj;

    @Column(name = "result")
    private Boolean result;

    @Column(name = "create_date")
    @CreationTimestamp
    private Date createDate;

    @Column(name = "update_date")
    @UpdateTimestamp
    private Date updateDate;

    @Column(name = "total_cash_value")
    private BigDecimal totalCashValue;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "contract_id")
    private List<ContractItem> contractItem;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Column(name = "contract_expiration")
    private Date contractExpiration;


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        Contract contract = (Contract) o;
        return id != null && Objects.equals(id, contract.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
