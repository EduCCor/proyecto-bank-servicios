package com.everis.springboot.fixedterm.dao;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

import com.everis.springboot.fixedterm.documents.FixedTermDocument;
import reactor.core.publisher.Mono;

public interface FixedTermDao extends ReactiveMongoRepository<FixedTermDocument, String>{
	
	Mono<FixedTermDocument> findByIdCliente(String client);

}
