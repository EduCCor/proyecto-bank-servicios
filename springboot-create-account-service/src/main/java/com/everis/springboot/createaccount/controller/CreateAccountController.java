package com.everis.springboot.createaccount.controller;

import java.util.Map;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.everis.springboot.createaccount.document.CreateAccountDocument;
import com.everis.springboot.createaccount.service.CreateAccountService;

import reactor.core.publisher.Mono;

@RefreshScope
@RestController
public class CreateAccountController {
	
	@Autowired
	private CreateAccountService accountService;
	
	@PostMapping("/saveAccount/{id}")
	public Mono<ResponseEntity<Map<String,Object>>> saveAccount(@PathVariable String id,@Valid @RequestBody CreateAccountDocument document){
		return accountService.saveAccount(id,document);
	}

	@GetMapping("/findAccount/{id}")
	public Mono<CreateAccountDocument> getProduct(@PathVariable("id") String id) {
		return accountService.findAccountsById(id);
	}
	

}
