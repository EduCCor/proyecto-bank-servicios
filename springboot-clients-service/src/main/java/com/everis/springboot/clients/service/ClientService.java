package com.everis.springboot.clients.service;

import java.util.Map;

import org.springframework.http.ResponseEntity;

import com.everis.springboot.clients.documents.ClientDocument;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ClientService {
	
	Mono<ResponseEntity<?>> saveClient(ClientDocument client);
	
	Flux<ClientDocument> findClients();
	
	Mono<ClientDocument> findClient(String id);
	
	Mono<ResponseEntity<Map<String,Object>>> updateClient(String id,ClientDocument client);
	
	ResponseEntity<String> deleteClient(String id);

}
