package com.webflux.reactiveapiwebflux.config;

import com.webflux.reactiveapiwebflux.controller.ConsultaHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.*;

@Configuration
public class ConsultaRouter {

    @Bean
    public RouterFunction<ServerResponse> route(ConsultaHandler handler) {
        return RouterFunctions
                .route(
                        GET("/consulta/{cpfCnpj}")
                        .and(accept(MediaType.APPLICATION_JSON)), handler::consultaByCpfCnpj);
    }
}
