package com.webflux.reactiveapiwebflux.controller;

import com.webflux.reactiveapiwebflux.entity.Emprestimo;
import com.webflux.reactiveapiwebflux.exception.CpfCnpjNotValidException;
import com.webflux.reactiveapiwebflux.service.EmprestimoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.math.BigDecimal;
import java.math.BigInteger;

@Controller
@RequestMapping("emprestimo")
public class EmprestimoController {

    @Autowired
    private EmprestimoService service;


    @GetMapping("teste")
    public String formularioEmprestimoTeste(final Model model){
        var teste = this.service.pmt(new BigDecimal("0.9489"), 12, new BigDecimal("1000.00"));
        model.addAttribute("Emprestimo", new Emprestimo());
        model.addAttribute("teste", teste);
        return "simular-emprestimo";
    }

    @GetMapping
    public String formularioEmprestimo(final Model model){
        model.addAttribute("Emprestimo", new Emprestimo());
        return "simular-emprestimo";
    }


    @PostMapping
    public String simularEmprestipo(Emprestimo emprestimo, final Model model) {
        try {
            model.addAttribute("Emprestimo", emprestimo);
            model.addAttribute("parcelaList", this.service.validaEmprestimo(emprestimo));
            return "simular-emprestimo";
        } catch (Exception e) {
            model.addAttribute("message", e.getMessage());
            model.addAttribute("Emprestimo", emprestimo);
            return "simular-emprestimo";
        }
    }
}
