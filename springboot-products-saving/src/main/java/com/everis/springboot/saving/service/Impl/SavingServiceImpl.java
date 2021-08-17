package com.everis.springboot.saving.service.Impl;

import com.everis.springboot.saving.dao.SavingDao;
import com.everis.springboot.saving.model.ProductDocument;
import com.everis.springboot.saving.model.SavingDocument;
import com.everis.springboot.saving.service.SavingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

@Service
public class SavingServiceImpl implements SavingService {

    @Autowired
    private SavingDao savingDao;

    @Autowired
    private WebClient.Builder webClientBuilder;

    @Override
    public Mono<ResponseEntity<Map<String, Object>>> saveSaving(String id, SavingDocument saving) {
        Map<String, Object> response = new HashMap<>();

        Mono<ProductDocument> product = webClientBuilder.build().get()
                .uri("http://localhost:8090/api/product/product/"+id)
//                .uri("http://localhost:65051/product/"+id)
                .retrieve()
                .bodyToMono(ProductDocument.class);
        System.out.println(product);
        return savingDao.findByClient(id).collectList().flatMap( productos -> {
            Mono<ResponseEntity<Map<String,Object>>> res = product.flatMap(c -> {
                        if (productos.size() == 0) {
                            saving.setProduct(c.getId());
                            saving.setClient(c.getClient());
                            if(c.getAccount_type().equals("Personal")){
                                return savingDao.save(saving).flatMap(p ->{
                                    response.put("savingSaved", p);
                                    response.put("mensaje", "Cuenta de Ahorros registrada con exito");
                                    return Mono.just(new ResponseEntity<Map<String, Object>>(response, HttpStatus.OK));
                                });
                            }else{
                                response.put("mensaje", "No se puede guardar porque es tipo Empresarial");
                                return Mono.just(new ResponseEntity<Map<String, Object>>(response, HttpStatus.OK));
                            }


                        } else {
                            response.put("mensaje", "No se puede guardar porque ya existe este producto");
                            return Mono.just(new ResponseEntity<Map<String, Object>>(response, HttpStatus.OK));
                        }
                    });

            return res;
        });
    }

    @Override
    public Flux<SavingDocument> findSavingsByProduct(String id) {
        return null;
    }

    @Override
    public Mono<SavingDocument> findSaving(String id) {
        return savingDao.findById(id);
    }


    @Override
    public Mono<ResponseEntity<Map<String, Object>>> updateSaving(String id, SavingDocument saving) {
        return null;
    }

    @Override
    public ResponseEntity<String> deleteSaving(String id) {
        return null;
    }
}
