package com.everis.springboot.saving.service;

import com.everis.springboot.saving.model.SavingDocument;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Map;

public interface SavingService {

    Mono<ResponseEntity<Map<String,Object>>> saveSaving(String id, SavingDocument saving);

    Flux<SavingDocument> findSavingsByProduct(String id);

//    Mono<SavingDocument> findSavingsById(String id);

    Mono<SavingDocument> findSaving(String id);

    Mono<ResponseEntity<Map<String,Object>>> updateSaving(String id,SavingDocument saving);

    ResponseEntity<String> deleteSaving(String id);

}
