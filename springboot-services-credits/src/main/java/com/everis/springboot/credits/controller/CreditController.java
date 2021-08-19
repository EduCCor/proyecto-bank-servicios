package com.everis.springboot.credits.controller;

import com.everis.springboot.credits.documents.CreditDocument;
import com.everis.springboot.credits.service.CreditService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import javax.validation.Valid;
import java.util.Map;

@RestController
public class CreditController {

    @Autowired
    private CreditService creditService;

    @PostMapping("/saveCredit/{idClient}")
    public Mono<ResponseEntity<Map<String,Object>>> saveCredit(@PathVariable String idClient, @Valid @RequestBody CreditDocument document){
        return creditService.saveCredit(idClient,document);
    }

    @PostMapping("/pay/{id}/{amount}")
    public Mono<ResponseEntity<Map<String,Object>>> payCredit(@PathVariable String id, @PathVariable Double amount){
        return creditService.payCredit(id, amount);
    }

    @PostMapping("/spend/{id}/{amount}")
    public Mono<ResponseEntity<Map<String,Object>>> spendCredit(@PathVariable String id, @PathVariable Double amount){
        return creditService.spendCredit(id, amount);
    }

    @GetMapping("/getBalance/{idCredit}")
    public Mono<ResponseEntity<Map<String,Object>>> getBalance(@PathVariable("idCredit") String idCredit) {
        return creditService.consultCredit(idCredit);
    }

    @GetMapping("/getOnlyCredits/{idClient}")
    public Mono<ResponseEntity<Map<String,Object>>> getOnlyCredits(@PathVariable("idClient") String idClient) {
        return creditService.getOnlyCredits(idClient);
    }

    @GetMapping("/getCreditCards/{idClient}")
    public Mono<ResponseEntity<Map<String,Object>>> getCreditCards(@PathVariable("idClient") String idClient) {
        return creditService.getCreditCards(idClient);
    }
}
