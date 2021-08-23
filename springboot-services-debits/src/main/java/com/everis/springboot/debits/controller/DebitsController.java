package com.everis.springboot.debits.controller;

import com.everis.springboot.debits.documents.AccountDocument;
import com.everis.springboot.debits.documents.DebitsDocument;
import com.everis.springboot.debits.service.DebitsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.Map;

@RestController
public class DebitsController {

    @Autowired
    private DebitsService debitsService;

    @PostMapping("/save/{idClient}/{numberCard}")
    public Mono<DebitsDocument> saveDebit(@PathVariable String idClient, @PathVariable String numberCard){
        return debitsService.createDebit(idClient, numberCard);
    }

    @PostMapping("/associate/{idDebitCard}")
    public Mono<ResponseEntity<Map<String, Object>>> associateAccount(@PathVariable String idDebitCard, @RequestBody AccountDocument account){
        return debitsService.accountsDebit(idDebitCard,account);
    }

    @PostMapping("/pay/{idDebitCard}/{amount}")
    public Mono<ResponseEntity<Map<String, Object>>> payWithAccount(@PathVariable String idDebitCard,@PathVariable Double amount){
        return debitsService.payWithAccount(idDebitCard,amount);
    }

    @GetMapping("/balance/{idDebitCard}")
    public Mono<ResponseEntity<Map<String, Object>>> consultBalanceDebitCard(@PathVariable String idDebitCard){
        return debitsService.searchBalanceDebit(idDebitCard);
    }
}
