package com.webflux.reactiveapiwebflux.controller;

import com.webflux.reactiveapiwebflux.entity.Contract;
import com.webflux.reactiveapiwebflux.exception.CpfCnpjNotValidException;
import com.webflux.reactiveapiwebflux.service.ContractService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("contract")
public class SaveContractController {

    @Autowired
    private ContractService service;


    @PostMapping
    public String saveContract(Contract contract, final Model model) {
        try {
            contract.setResult(Boolean.TRUE);
             service.saveNewContract(contract);
            model.addAttribute("messageOk", "Contract saved successfully!!");
            model.addAttribute("contract", new Contract());
            return "form";
        } catch (CpfCnpjNotValidException e) {
            model.addAttribute("message", e.getMessage());
        }
        return "form";
    }

    @GetMapping(path = "/edit/{id}")
    public String editContract(@PathVariable Long id, final Model model) {
        try {
            Contract contract = service.getEditContractById(id);
            model.addAttribute("contract", contract);
            return "form";
        } catch (CpfCnpjNotValidException e) {
            model.addAttribute("message", e.getMessage());
        }
        return "form";
    }
}
