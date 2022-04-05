package com.webflux.reactiveapiwebflux.config;

import com.webflux.reactiveapiwebflux.controller.ContractHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

import java.util.concurrent.Executors;

import static org.springframework.web.reactive.function.server.RequestPredicates.accept;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
public class ContractRouter {

    @Bean
    Scheduler jdbcScheduler(Environment env) {
        return Schedulers.fromExecutor(Executors.newFixedThreadPool(env.getRequiredProperty("jdbc.connection.pool.size", Integer.class)));
    }

    @Bean
    public RouterFunction<ServerResponse> contractRoute(ContractHandler handler) {
        return route()
                .GET("/contract/find/{cpfCnpj}", handler::contractByCpfCnpj)

                .POST("/contract", accept(MediaType.APPLICATION_FORM_URLENCODED), handler::saveContract)
                .GET("/contract", handler::findAllContract)
                .GET("/contract/{id}", handler::contractById)
                .PUT("/contract/{id}", handler::updateContract)
                .DELETE("/contract/{id}", handler::deleteContractById)

                .GET("/contract/delete/{id}", handler::deleteContractById)
                .GET("/contract/edit/{id}", handler::updateContract)
                .GET("/contract-new", handler::newContract)
                .build();
    }
}
