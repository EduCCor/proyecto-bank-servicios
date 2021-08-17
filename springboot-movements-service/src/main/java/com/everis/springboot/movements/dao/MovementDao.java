package com.everis.springboot.movements.dao;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

import com.everis.springboot.movements.documents.MovementDocument;

public interface MovementDao extends ReactiveMongoRepository<MovementDocument, String> {

}
