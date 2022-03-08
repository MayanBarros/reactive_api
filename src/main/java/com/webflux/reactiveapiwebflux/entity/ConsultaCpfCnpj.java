package com.webflux.reactiveapiwebflux.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.util.Date;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
@Entity
@Table(name = "consulta_cpf_cnpj", schema = "reactive_api")
public class ConsultaCpfCnpj {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @NotBlank(message = "CPF/CNPJ cannot be blank!")
    @Column(name = "cpf_cnpj")
    private String cpfCnpj;

    @Column(name = "result")
    private Boolean result;

    @Column(name = "create_date")
    private Date createDate;

    @Column(name = "update_date")
    private Date updateDate;
}
