package com.everis.springboot.saving.controller;

import com.everis.springboot.saving.model.SavingDocument;
import com.everis.springboot.saving.service.SavingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import javax.validation.Valid;
import java.util.Map;

@RestController
public class SavingController {

    @Autowired
    private SavingService savingService;

    @PostMapping("/saveSaving/{id}")
    public Mono<ResponseEntity<Map<String,Object>>> saveSavings(@PathVariable String id, @Valid @RequestBody SavingDocument saving){
        System.out.println("Entro al metodo guardar Saving");
        return savingService.saveSaving(id, saving);
    }

//    @GetMapping("/saving/{id}")
//    public Mono<SavingDocument> getSaving(@PathVariable("id") String id) {
//        return saving.findSaving(id);
//    }
}
