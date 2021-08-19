package com.everis.springboot.current.service;

import com.everis.springboot.current.model.CurrentDocument;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Map;

public interface CurrentService {

    Flux<CurrentDocument> getAllCurrentAccount();

    Mono<CurrentDocument> createCurrentAccount(CurrentDocument currentDocument);

    Mono<CurrentDocument> findById(String  idCurrentAccount);

    Flux<CurrentDocument> findByClientId(String clientId);

    Mono<CurrentDocument> save(CurrentDocument currentDocument);

    Mono<Void> deleteById(String  idCurrentAccount);

    Mono<ResponseEntity<Map<String,Object>>> depositar(String idCuenta, Double cantidad);

    Mono<ResponseEntity<Map<String,Object>>> retirar(String idCuenta,Double cantidad);

    Mono<ResponseEntity<Map<String,Object>>> consultarSaldo(String idCliente);
}
