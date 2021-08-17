package com.everis.springboot.fixedterm.service;

import java.util.Map;

import org.springframework.http.ResponseEntity;

import com.everis.springboot.fixedterm.documents.FixedTermDocument;

import reactor.core.publisher.Mono;

public interface FixedTermService {
	
	Mono<FixedTermDocument> createAccount(FixedTermDocument documetn);
	
	Mono<ResponseEntity<Map<String,Object>>> depositar(String idCuenta,Double cantidad);
	
	Mono<ResponseEntity<Map<String,Object>>> retirar(String idCuenta,Double cantidad);
	
	Mono<ResponseEntity<Map<String,Object>>> consultarSaldo(String idCliente);

}
