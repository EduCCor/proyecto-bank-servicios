package com.everis.springboot.createaccount.service;

import java.util.Map;
import org.springframework.http.ResponseEntity;

import com.everis.springboot.createaccount.document.CreateAccountDocument;

import reactor.core.publisher.Mono;

public interface CreateAccountService {
	
	Mono<ResponseEntity<Map<String,Object>>> saveAccount(String id, CreateAccountDocument account);

	Mono<CreateAccountDocument> findAccountsById(String id);

}
