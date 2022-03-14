package com.webflux.reactiveapiwebflux.repository;

import com.webflux.reactiveapiwebflux.entity.ConsultaCpfCnpj;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ConsultaRepository extends JpaRepository<ConsultaCpfCnpj, Long> {
    public ConsultaCpfCnpj findByCpfCnpj(String cpfCnpj);
}
