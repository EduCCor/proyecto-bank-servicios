package com.everis.springboot.clients.controller;

import java.util.Map;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.everis.springboot.clients.documents.ClientDocument;
import com.everis.springboot.clients.service.ClientService;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RefreshScope
@RestController
public class ClientController {
	
	@Autowired
	private ClientService clientService;
	
	@PostMapping("/saveClient")
	public Mono<ResponseEntity<?>> saveClient(@Valid @RequestBody ClientDocument client){
		return clientService.saveClient(client);
	}

	@GetMapping("/allClients")
	public Flux<ClientDocument> getAllClients() {
		return clientService.findClients();
	}
	
	@GetMapping("/client/{id}")
	public Mono<ClientDocument> getClient(@PathVariable("id") String id) {
		return clientService.findClient(id);
	}

	@PutMapping("/updateClient/{id}")
	public Mono<ResponseEntity<Map<String, Object>>> updateClient(@PathVariable("id") String id, @RequestBody ClientDocument client) {
		return clientService.updateClient(id, client);
	}

	@DeleteMapping("/deleteClient/{id}")
	public ResponseEntity<String> deleteClient(@PathVariable("id") String id) {
		return clientService.deleteClient(id);
	}

}
