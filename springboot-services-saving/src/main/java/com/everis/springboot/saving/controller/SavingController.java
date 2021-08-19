package com.everis.springboot.saving.controller;

import com.everis.springboot.saving.model.SavingDocument;
import com.everis.springboot.saving.service.SavingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.validation.Valid;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping
public class SavingController {

    @Autowired
    private SavingService savingService;

    public static final Logger LOGGER = LoggerFactory.getLogger(SavingController.class);

    @GetMapping()
    public Flux<SavingDocument> getAllSaving(){
        Flux<SavingDocument> currentAccounts = savingService.getAllSavingAccount().map(currentAccount -> {
            return currentAccount;
        });

        return currentAccounts;
    }


    @PostMapping()
    public Mono<SavingDocument> saveSavings(@Valid @RequestBody SavingDocument savingDocument){

        try {
            savingDocument.setCreateSaving(new Date());
            Mono<SavingDocument> save = savingService.save(savingDocument);
            LOGGER.info("Se ingreso correctamente");

            return save;


        } catch (Exception e) {
            LOGGER.info("Error: " + e);
            return null;
        }

        //return savingService.saveSaving(id, saving);
    }

    @PutMapping()
    public Mono<ResponseEntity<Mono<SavingDocument>>> updateSavingAccount(@RequestBody SavingDocument savingDocument) {
        try {
            Mono<SavingDocument> updatedSavingDocument= savingService.findById(savingDocument.getId())
                    .flatMap(c ->{
                        savingDocument.setModifiedSaving(new Date ());

                        return savingService.save(savingDocument);
                    })
                    .cast(SavingDocument.class);

            if ( updatedSavingDocument == null) {
                LOGGER.info("No se econtró el registro de la cuenta corriente");

                Map<String, Object> params = new HashMap<String, Object>();
                params.put("mensaje", "No existe la cuenta");

                return Mono.just(new ResponseEntity<>(null, HttpStatus.NOT_FOUND));
            }else {
                LOGGER.info("Se modificó correctamente");
                Map<String, Object> params = new HashMap<String, Object>();

                params.put("mensaje", "No existe la cuenta");

                return Mono.just(new ResponseEntity<>(updatedSavingDocument, HttpStatus.NOT_FOUND));
            }



        }
        catch (Exception e) {
            LOGGER.error("Error: " + e);
            return Mono.just(new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR));
        }


    }

    @DeleteMapping()
    public Mono<ResponseEntity<Void>> deleteSavingAccount(@RequestParam(name="idSavingDocument",required = true) String idSavingDocument) {

        return savingService.findById(idSavingDocument).flatMap(c ->{
            return savingService.deleteById(idSavingDocument)
                    .then(Mono.just(new ResponseEntity<Void>(HttpStatus.NO_CONTENT)));
        }).defaultIfEmpty(new ResponseEntity<Void>(HttpStatus.NOT_FOUND));
    }
    @PostMapping("/deposit/{id}/{amount}")
    public Mono<ResponseEntity<Map<String,Object>>> deposit(@PathVariable String id, @PathVariable Double amount){
        return savingService.depositar(id, amount);
    }

    @PostMapping("/retirement/{id}/{amount}")
    public Mono<ResponseEntity<Map<String,Object>>> retirement(@PathVariable String id, @PathVariable Double amount){
        return savingService.retirar(id, amount);
    }

    @GetMapping("/getBalance/{id}")
    public Mono<ResponseEntity<Map<String,Object>>> getBalance(@PathVariable("id") String id) {
        return savingService.consultarSaldo(id);
    }

    @GetMapping ("/getAccount/{id}")
    public Mono<SavingDocument> getAccount (@PathVariable("id") String id){

        return savingService.findById(id);
    }
}
