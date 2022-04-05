package com.webflux.reactiveapiwebflux.service;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
public class ConsultaServiceTest {

    @InjectMocks
    private ContractService clientService;

//    @Test
//    void testMethodWithCpfValid() {
//        boolean isValid = clientService.validCpf("45721877898");
//        assertTrue(isValid);
//
//        isValid = clientService.validCpf("26666761880");
//        assertTrue(isValid);
//    }
//
//    @Test
//    void testMethodWithCpfNotValid() {
//        boolean isValid = clientService.validCpf("00000000000");
//        assertFalse(isValid);
//
//        isValid = clientService.validCpf("12345678912");
//        assertFalse(isValid);
//    }
//
//    @Test
//    void testMethodWithCnpjValid() {
//        boolean isValid = clientService.validCnpj("18823384000108");
//        assertTrue(isValid);
//
//        isValid = clientService.validCnpj("50953922000191");
//        assertTrue(isValid);
//    }
//
//    @Test
//    void testMethodWithCnpjNotValid() {
//        boolean isValid = clientService.validCnpj("000000000");
//        assertFalse(isValid);
//
//        isValid = clientService.validCnpj("12345678912345");
//        assertFalse(isValid);
//    }
}
