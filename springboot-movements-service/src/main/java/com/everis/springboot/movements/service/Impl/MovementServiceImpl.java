package com.everis.springboot.movements.service.Impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.everis.springboot.movements.dao.MovementDao;
import com.everis.springboot.movements.documents.MovementDocument;
import com.everis.springboot.movements.service.MovementService;

import reactor.core.publisher.Mono;

@Service
public class MovementServiceImpl implements MovementService {
	
	@Autowired
	private MovementDao movementDao;

	@Override
	public Mono<MovementDocument> saveMovement(MovementDocument movement) {
		return movementDao.save(movement);
	}

}
