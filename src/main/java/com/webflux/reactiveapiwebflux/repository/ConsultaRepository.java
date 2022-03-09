package com.webflux.reactiveapiwebflux.repository;

import com.webflux.reactiveapiwebflux.entity.ConsultaCpfCnpj;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ConsultaRepository extends CrudRepository<ConsultaCpfCnpj, Integer> {
}
