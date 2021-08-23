package com.everis.springboot.debits.dao;

import com.everis.springboot.debits.documents.DebitsDocument;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface DebitsDao extends ReactiveMongoRepository<DebitsDocument, String> {
}
