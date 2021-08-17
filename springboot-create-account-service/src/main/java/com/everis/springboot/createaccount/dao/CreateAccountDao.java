package com.everis.springboot.createaccount.dao;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

import com.everis.springboot.createaccount.document.CreateAccountDocument;

import reactor.core.publisher.Flux;

public interface CreateAccountDao extends ReactiveMongoRepository<CreateAccountDocument, String> {
	
	Flux<CreateAccountDocument> findByClient(String client);

}
