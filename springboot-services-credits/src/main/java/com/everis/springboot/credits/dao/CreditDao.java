package com.everis.springboot.credits.dao;

import com.everis.springboot.credits.documents.CreditDocument;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface CreditDao extends ReactiveMongoRepository<CreditDocument, String> {

    Flux<CreditDocument> findByIdClient(String idClient);

    Mono<CreditDocument> findByIdClientAndCreditType(String idClient, String creditType);
}
