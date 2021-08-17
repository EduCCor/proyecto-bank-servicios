package com.everis.springboot.fixedterm.controller;

import java.util.Map;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import com.everis.springboot.fixedterm.documents.FixedTermDocument;
import com.everis.springboot.fixedterm.service.FixedTermService;

import reactor.core.publisher.Mono;

@RestController
public class FixedTermController {
	
	@Autowired
	private FixedTermService fixedTermService;
	
	@PostMapping("/saveAccount")
	public Mono<FixedTermDocument> saveAccount(@Valid @RequestBody FixedTermDocument document){
		System.out.println("Entro al metodo guardar cuenta");
		return fixedTermService.createAccount(document);
	}
	
	@PostMapping("/deposit/{id}/{amount}")
	public Mono<ResponseEntity<Map<String,Object>>> deposit(@PathVariable String id, @PathVariable Double amount){
		System.out.println("Entro al metodo guardar cuenta");
		return fixedTermService.depositar(id, amount);
	}
	
	@PostMapping("/retirement/{id}/{amount}")
	public Mono<ResponseEntity<Map<String,Object>>> retirement(@PathVariable String id, @PathVariable Double amount){
		System.out.println("Entro al metodo guardar cuenta");
		return fixedTermService.retirar(id, amount);
	}
	
	@GetMapping("/getBalance/{id}")
	public Mono<ResponseEntity<Map<String,Object>>> getBalance(@PathVariable("id") String id) {
		return fixedTermService.consultarSaldo(id);
	}

}
