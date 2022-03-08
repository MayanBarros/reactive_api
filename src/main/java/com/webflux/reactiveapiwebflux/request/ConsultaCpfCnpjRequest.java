package com.webflux.reactiveapiwebflux.request;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ConsultaCpfCnpjRequest {
    private String cpfCnpj;
}
