package com.everis.springboot.saving.service.Impl;

import com.everis.springboot.saving.dao.SavingDao;
import com.everis.springboot.saving.model.MovementDocument;
import com.everis.springboot.saving.model.SavingDocument;
import com.everis.springboot.saving.service.SavingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class SavingServiceImpl implements SavingService {

    @Autowired
    private SavingDao savingDao;

    @Autowired
    private WebClient.Builder webClientBuilder;

    public static final Logger LOGGER = LoggerFactory.getLogger(SavingServiceImpl.class);

    @Value("${everis.saving.cantidad.movimientos}")
    private Integer amountOfMovements;

    @Value("${everis.saving.comision.movimientos}")
    private double comissionPerMovement;

    @Value("${everis.saving.url.gateway}")
    private String urlGateway;


    @Override
    public Flux<SavingDocument> getAllSavingAccount() {
        Flux<SavingDocument> lsavingAccount = savingDao.findAll();
        return lsavingAccount;
    }

    @Override
    public Mono<SavingDocument> save(SavingDocument saving) {
        if(saving.getId() != null) {
            return Mono.error(new IllegalArgumentException("Id of New SavingAccount be null"));
        }
        return savingDao.save(saving);
    }

    @Override
    public Mono<SavingDocument> findById(String idSavingDocument) {
        return savingDao.findById(idSavingDocument);
    }

    @Override
    public Mono<Void> deleteById(String idSavingDocument) {
        return savingDao.deleteById(idSavingDocument);
    }

    @Override
    public Mono<ResponseEntity<Map<String, Object>>> consultarSaldo(String idSavingDocument) {
        Map<String, Object> response = new HashMap<>();

        return savingDao.findById(idSavingDocument).flatMap( c -> {


            response.put("mensaje", "El saldo de la cuenta es: S/."+c.getAmountSaving());
            return Mono.just(new ResponseEntity<Map<String,Object>>(response, HttpStatus.OK));

        }).defaultIfEmpty(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @Override
    public Mono<ResponseEntity<Map<String, Object>>> retirar(String idSavingDocument, Double cant) {
        Map<String, Object> response = new HashMap<>();
        Calendar calendar = Calendar.getInstance();
        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss");

        return webClientBuilder.build().get()
                .uri(urlGateway+"/api/movement/numberOfMovements?idCuenta=" + idSavingDocument)
                .retrieve()
                .bodyToMono(Long.class)
                .flatMap(number->{
                    if (number <= amountOfMovements) {
                        return savingDao.findById(idSavingDocument).flatMap( c -> {


                            if(c.getAmountSaving() - cant < 0) {
                                response.put("mensaje", "No puede realizar este retiro ya que no cuenta con el saldo suficiente");
                                return Mono.just(new ResponseEntity<Map<String,Object>>(response, HttpStatus.OK));
                            }else {
                                c.setAmountSaving(c.getAmountSaving() - cant );
                                return savingDao.save(c).flatMap(acc -> {

                                    Date date = Calendar.getInstance().getTime();
                                    MovementDocument movement = MovementDocument.builder()
                                            .tipoMovimiento("Retiro")
                                            .tipoProducto("Cuenta Corriente")
                                            .fechaMovimiento(dateFormat.format(date))

                                            .idCuenta(idSavingDocument)
                                            .idCliente(acc.getIdClient())
                                            .build();

                                    webClientBuilder.build().post()
                                            .uri(urlGateway+"/api/movement/saveMovement")
                                            .body(Mono.just(movement), MovementDocument.class)
                                            .retrieve().bodyToMono(MovementDocument.class).subscribe();

                                    response.put("mensaje", "Se hizo el retiro exitosamente");
                                    response.put("cuenta", acc);
                                    return Mono.just(new ResponseEntity<Map<String,Object>>(response, HttpStatus.OK));
                                });
                            }

                        }).defaultIfEmpty(new ResponseEntity<>(HttpStatus.NOT_FOUND));

                    }else{
                        return savingDao.findById(idSavingDocument).flatMap( c -> {


                            if(c.getAmountSaving() - cant - comissionPerMovement< 0) {
                                response.put("mensaje", "No puede realizar este retiro ya que no cuenta con el saldo suficiente");
                                return Mono.just(new ResponseEntity<Map<String,Object>>(response, HttpStatus.OK));
                            }else {

                                c.setAmountSaving(c.getAmountSaving() - cant - comissionPerMovement);
                                return savingDao.save(c).flatMap(acc -> {

                                    Date date = Calendar.getInstance().getTime();
                                    MovementDocument movement = MovementDocument.builder()
                                            .tipoMovimiento("Retiro")
                                            .tipoProducto("Cuenta Corriente")
                                            .fechaMovimiento(dateFormat.format(date))
                                            .comission(comissionPerMovement)
                                            .idCuenta(idSavingDocument)
                                            .idCliente(acc.getIdClient())
                                            .build();

                                    webClientBuilder.build().post()
                                            .uri(urlGateway+"/api/movement/saveMovement")
                                            .body(Mono.just(movement), MovementDocument.class)
                                            .retrieve().bodyToMono(MovementDocument.class).subscribe();

                                    response.put("mensaje", "Se hizo el retiro exitosamente");
                                    response.put("cuenta", acc);
                                    return Mono.just(new ResponseEntity<Map<String,Object>>(response, HttpStatus.OK));
                                });
                            }

                        }).defaultIfEmpty(new ResponseEntity<>(HttpStatus.NOT_FOUND));
                    }
                });
    }

    @Override
    public Mono<ResponseEntity<Map<String, Object>>> depositar(String idSavingDocument, Double cant) {
        Map<String, Object> response = new HashMap<>();
        Calendar calendar = Calendar.getInstance();
        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss");

        return webClientBuilder.build().get()
                .uri(urlGateway+"/api/movement/numberOfMovements?idCuenta=" + idSavingDocument)
                .retrieve().bodyToMono(Long.class).flatMap(number->{
                    if(number <= amountOfMovements){

                        return savingDao.findById(idSavingDocument).flatMap( c -> {
                            c.setAmountSaving(c.getAmountSaving() + cant);
                            return savingDao.save(c).flatMap(acc -> {

                                Date date = Calendar.getInstance().getTime();
                                MovementDocument movement = MovementDocument.builder()
                                        .tipoMovimiento("Deposito")
                                        .tipoProducto("Cuenta Ahorros")
                                        .fechaMovimiento(dateFormat.format(date))
                                        .idCuenta(idSavingDocument)
                                        .idCliente(acc.getIdClient())
                                        .build();

                                webClientBuilder.build().post()
                                        .uri("http://localhost:8090/api/movement/saveMovement")
                                        .body(Mono.just(movement), MovementDocument.class)
                                        .retrieve().bodyToMono(MovementDocument.class).subscribe();


                                response.put("mensaje", "Se hizo el deposito exitosamente");
                                response.put("cuenta", acc);
                                return Mono.just(new ResponseEntity<Map<String,Object>>(response, HttpStatus.OK));
                            });

                        }).defaultIfEmpty(new ResponseEntity<>(HttpStatus.NOT_FOUND));

                    }else{
                        return  savingDao.findById(idSavingDocument).flatMap( c -> {
                            c.setAmountSaving(c.getAmountSaving() + cant -comissionPerMovement);
                            return savingDao.save(c).flatMap(acc -> {

                                Date date = Calendar.getInstance().getTime();
                                MovementDocument movement = MovementDocument.builder()
                                        .tipoMovimiento("Deposito")
                                        .tipoProducto("Cuenta Ahorros")
                                        .fechaMovimiento(dateFormat.format(date))
                                        .idCuenta(idSavingDocument)
                                        .comission(comissionPerMovement)
                                        .idCliente(acc.getIdClient())
                                        .build();

                                webClientBuilder.build().post()
                                        .uri(urlGateway+"/api/movement/saveMovement")
                                        .body(Mono.just(movement), MovementDocument.class)
                                        .retrieve().bodyToMono(MovementDocument.class).subscribe();


                                response.put("mensaje", "Se hizo el deposito exitosamente");
                                response.put("cuenta", acc);
                                return Mono.just(new ResponseEntity<Map<String,Object>>(response, HttpStatus.OK));
                            });

                        }).defaultIfEmpty(new ResponseEntity<>(HttpStatus.NOT_FOUND));
                    }
                });
    }
}
