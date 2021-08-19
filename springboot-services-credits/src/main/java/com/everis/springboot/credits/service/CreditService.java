package com.everis.springboot.credits.service;

import com.everis.springboot.credits.documents.CreditDocument;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Mono;

import java.util.Map;

public interface CreditService {

    Mono<ResponseEntity<Map<String,Object>>> saveCredit(String idClient, CreditDocument account);

    Mono<ResponseEntity<Map<String,Object>>> payCredit(String idCredit,Double cantidad);

    Mono<ResponseEntity<Map<String,Object>>> spendCredit(String idCredit,Double cantidad);

    Mono<ResponseEntity<Map<String,Object>>> consultCredit(String idCredit);

    Mono<ResponseEntity<Map<String, Object>>> getOnlyCredits(String idClient);

    Mono<ResponseEntity<Map<String, Object>>> getCreditCards(String idClient);
}
