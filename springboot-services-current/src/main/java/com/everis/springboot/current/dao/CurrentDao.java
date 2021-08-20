package com.everis.springboot.current.dao;

import com.everis.springboot.current.model.CurrentDocument;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;

public interface CurrentDao extends ReactiveMongoRepository<CurrentDocument, String> {
    Flux<CurrentDocument> findByIdClient(String idClient);
}
