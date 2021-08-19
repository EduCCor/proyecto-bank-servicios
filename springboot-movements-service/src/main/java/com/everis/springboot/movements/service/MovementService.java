package com.everis.springboot.movements.service;

import com.everis.springboot.movements.documents.MovementDocument;

import reactor.core.publisher.Mono;

public interface MovementService {
	
	Mono<MovementDocument> saveMovement(MovementDocument movement);

	Mono<Long> getNumberOfMovements(String idCuenta);

}
