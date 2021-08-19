package com.everis.springboot.movements.controller;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.everis.springboot.movements.documents.MovementDocument;
import com.everis.springboot.movements.service.MovementService;

import reactor.core.publisher.Mono;

@RestController
public class MovementController {
	
	@Autowired
	private MovementService movementService;
	
	@PostMapping("/saveMovement")
	public Mono<MovementDocument> saveMovement(@Valid @RequestBody MovementDocument movement){
		System.out.println("Entro al metodo guardar cuenta");
		return movementService.saveMovement(movement);
	}

	@GetMapping("/numberOfMovements")
	public Mono<Long> numberOfMovements(@RequestParam(name="idCuenta") String idCuenta){
		System.out.println("Entro al metodo guardar cuenta");
		return movementService.getNumberOfMovements(idCuenta);
	}

}
