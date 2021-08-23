package com.everis.springboot.current.controller;

import com.everis.springboot.current.model.CurrentDocument;
import com.everis.springboot.current.service.CurrentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping
public class CurrentController {

    @Autowired
    private CurrentService currentService;

    private static final Logger LOGGER = LoggerFactory.getLogger(CurrentController.class);

    @GetMapping("/getAccount/{idAccount}")
    public Mono<CurrentDocument> getCurrentAccount(@PathVariable String idAccount) {
        return currentService.getCurrentAccount(idAccount);
    }

    @GetMapping()
    public Flux<CurrentDocument> getAllCurrentAccount() {

        Flux<CurrentDocument> currentAccounts = currentService.getAllCurrentAccount().map(currentAccount -> {
            return currentAccount;
        });
        return currentAccounts;
    }

    @PostMapping()
    public Mono<CurrentDocument> saveCurrentAccount(@RequestBody CurrentDocument currentDocument) {
        try {
            currentDocument.setCreateCurrent(new Date());
            Mono<CurrentDocument> save = currentService.save(currentDocument);
            LOGGER.info("Se ingresó correctamente");

            return save;


        } catch (Exception e) {
            LOGGER.error("Error: " + e);
            return null;
        }
    }

    @PutMapping()
    public Mono<ResponseEntity<Mono<CurrentDocument>>> updateCurrentAccount(@RequestBody CurrentDocument currentDocument) {
        try {
            Mono<CurrentDocument> updatedCurrentDocument= currentService.findById(currentDocument.getId()).flatMap(c ->{

                        currentDocument.setModifiedCurrent(new Date());

                        return currentService.save(currentDocument);})
                    .cast(CurrentDocument.class) ;
            if ( updatedCurrentDocument == null) {
                LOGGER.info("No se econtró el registro de la cuenta corriente");

                Map<String, Object> params = new HashMap<String, Object>();
                params.put("mensaje", "No existe la cuenta");

                return Mono.just(new ResponseEntity<>(null, HttpStatus.NOT_FOUND));
            }else {
                LOGGER.info("Se modificó correctamente");
                Map<String, Object> params = new HashMap<String, Object>();

                params.put("mensaje", "Existe la cuenta");

                return Mono.just(new ResponseEntity<>(updatedCurrentDocument, HttpStatus.NOT_FOUND));
            }

        }
        catch (Exception e) {
            LOGGER.error("Error: " + e);
            return Mono.just(new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }

    @DeleteMapping()
    public Mono<ResponseEntity<Void>> deleteCurrentAccount(@RequestParam(name="idCurrentAccount",required = true) String idCurrentAccount) {

        return currentService.findById(idCurrentAccount).flatMap(c ->{
            return currentService.deleteById(idCurrentAccount).then(Mono.just(new ResponseEntity<Void>(HttpStatus.NO_CONTENT))) ;
        }).defaultIfEmpty(new ResponseEntity<Void>(HttpStatus.NOT_FOUND));
    }

    @PostMapping("/deposit/{id}/{amount}")
    public Mono<ResponseEntity<Map<String,Object>>> deposit(@PathVariable String id, @PathVariable Double amount){
        System.out.println("Entro al metodo guardar cuenta");
        return currentService.depositar(id, amount);
    }

    @PostMapping("/retirement/{id}/{amount}")
    public Mono<ResponseEntity<Map<String,Object>>> retirement(@PathVariable String id, @PathVariable Double amount){
        System.out.println("Entro al metodo guardar cuenta");
        return currentService.retirar(id, amount);
    }

    @GetMapping("/getBalance/{id}")
    public Mono<ResponseEntity<Map<String,Object>>> getBalance(@PathVariable("id") String id) {
        return currentService.consultarSaldo(id);
    }
}
