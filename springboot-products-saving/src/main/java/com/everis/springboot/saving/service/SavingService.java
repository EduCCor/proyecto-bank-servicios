package com.everis.springboot.saving.service;

import com.everis.springboot.saving.model.SavingDocument;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Map;

public interface SavingService {

    Flux<SavingDocument> getAllSavingAccount();

    Mono<SavingDocument> save(SavingDocument saving);

    Mono<SavingDocument> findById(String idSavingDocument);

    Mono<Void> deleteById(String idSavingDocument);

    Mono<ResponseEntity<Map<String,Object>>> consultarSaldo(String idSavingDocument);

    Mono<ResponseEntity<Map<String,Object>>> retirar(String idSavingDocument,Double cant);

    Mono<ResponseEntity<Map<String,Object>>> depositar(String idSavingDocument,Double cant);

}
