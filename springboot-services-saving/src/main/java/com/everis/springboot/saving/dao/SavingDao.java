package com.everis.springboot.saving.dao;

import com.everis.springboot.saving.model.SavingDocument;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;

public interface SavingDao extends ReactiveMongoRepository<SavingDocument, String> {
}
