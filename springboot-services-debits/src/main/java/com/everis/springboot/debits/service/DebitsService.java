package com.everis.springboot.debits.service;

import com.everis.springboot.debits.documents.AccountDocument;
import com.everis.springboot.debits.documents.DebitsDocument;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Mono;

import java.util.Map;

public interface DebitsService {
    Mono<DebitsDocument> createDebit(String idClient, String numberCard);

    Mono<ResponseEntity<Map<String, Object>>> accountsDebit(String idDebitCard, AccountDocument account);

    Mono<ResponseEntity<Map<String, Object>>> payWithAccount(String idDebitCard, Double amount);

    Mono<ResponseEntity<Map<String, Object>>> searchBalanceDebit(String idDebitCard);
}
