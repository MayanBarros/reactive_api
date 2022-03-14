package com.webflux.reactiveapiwebflux.config;

import com.webflux.reactiveapiwebflux.controller.ConsultaHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

import java.util.concurrent.Executors;

import static org.springframework.web.reactive.function.server.RequestPredicates.*;

@Configuration
public class ConsultaRouter {

    @Bean
    Scheduler jdbcScheduler(Environment env) {
        return Schedulers.fromExecutor(Executors.newFixedThreadPool(env.getRequiredProperty("jdbc.connection.pool.size", Integer.class)));
    }

    @Bean
    public RouterFunction<ServerResponse> route(ConsultaHandler handler) {
        return RouterFunctions.route(
                        GET("/consulta/{cpfCnpj}").and(accept(MediaType.APPLICATION_JSON)), handler::consultaByCpfCnpj)
                .andRoute(GET("/consulta").and(accept(MediaType.APPLICATION_JSON)), handler::findAllConsulta)
                .andRoute(GET("/consulta/{id}").and(accept(MediaType.APPLICATION_JSON)), handler::consultaById)
                .andRoute(POST("/consulta").and(accept(MediaType.APPLICATION_JSON)), handler::saveConsulta)
                .andRoute(DELETE("/consulta/{id}").and(accept(MediaType.APPLICATION_JSON)), handler::deleteConsultaById);
    }
}
