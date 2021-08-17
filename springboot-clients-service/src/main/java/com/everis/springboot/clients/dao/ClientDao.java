package com.everis.springboot.clients.dao;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

import com.everis.springboot.clients.documents.ClientDocument;

public interface ClientDao extends ReactiveMongoRepository<ClientDocument, String> {

}
