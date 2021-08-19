package com.everis.springboot.current.service.Impl;

import com.everis.springboot.current.dao.CurrentDao;
import com.everis.springboot.current.model.CurrentDocument;
import com.everis.springboot.current.model.MovementDocument;
import com.everis.springboot.current.service.CurrentService;
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
public class CurrentServiceImpl implements CurrentService {

    @Autowired
    private CurrentDao currentDao;

    @Autowired
    private WebClient.Builder webClientBuilder;

    @Value("${everis.current.cantidad.movimientos}")
    private Integer amountOfMovements;

    @Value("${everis.current.comision.movimientos}")
    private double comissionPerMovement;

    @Value("${everis.current.url.gateway}")
    private String urlGateway;

    @Override
    public Flux<CurrentDocument> getAllCurrentAccount() {
        Flux<CurrentDocument> lcurrentDocument = currentDao.findAll();
        return lcurrentDocument;
    }

    @Override
    public Mono<CurrentDocument> createCurrentAccount(CurrentDocument currentDocument) {
        if(currentDocument.getId() != null) {
            return Mono.error(new IllegalArgumentException("Id of New CurrentAccount be null"));
        }
        return currentDao.save(currentDocument);
    }

    @Override
    public Mono<CurrentDocument> findById(String idCurrentAccount) {
        return currentDao.findById(idCurrentAccount);
    }

    @Override
    public Flux<CurrentDocument> findByClientId(String clientId) {
        Flux<CurrentDocument> lcurrentDocument = currentDao.findByClientId(clientId) ;
        return lcurrentDocument;
    }

    @Override
    public Mono<CurrentDocument> save(CurrentDocument currentDocument) {
        return currentDao.save(currentDocument);
    }

    @Override
    public Mono<Void> deleteById(String idCurrentAccount) {
        return currentDao.deleteById(idCurrentAccount);
    }

    @Override
    public Mono<ResponseEntity<Map<String, Object>>> depositar(String idCuenta, Double cantidad) {

        Map<String, Object> response = new HashMap<>();
        Calendar calendar = Calendar.getInstance();
        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss");

        return webClientBuilder.build().get()
                .uri(urlGateway+"/api/movement/numberOfMovements?idCuenta="+idCuenta)
                .retrieve().bodyToMono(Long.class).flatMap(number->{
                    if (number <= amountOfMovements) {

                        return currentDao.findById(idCuenta).flatMap( c -> {
                            c.setAmountCurrent(c.getAmountCurrent() + cantidad);
                            return currentDao.save(c).flatMap(acc -> {
                                Date date = Calendar.getInstance().getTime();
                                MovementDocument movement = MovementDocument.builder()
                                        .tipoMovimiento("Deposito")
                                        .tipoProducto("Cuenta Corriente")
                                        .fechaMovimiento(dateFormat.format(date))
                                        .idCuenta(idCuenta)
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
                    }else{
                        return currentDao.findById(idCuenta).flatMap( c -> {
                            c.setAmountCurrent(c.getAmountCurrent() + cantidad-comissionPerMovement);
                            return currentDao.save(c).flatMap(acc -> {
                                Date date = Calendar.getInstance().getTime();
                                MovementDocument movement = MovementDocument.builder()
                                        .tipoMovimiento("Deposito")
                                        .tipoProducto("Cuenta Corriente")
                                        .fechaMovimiento(dateFormat.format(date))
                                        .comission(comissionPerMovement)
                                        .idCuenta(idCuenta)
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

    @Override
    public Mono<ResponseEntity<Map<String, Object>>> retirar(String idCuenta, Double cantidad) {
        Map<String, Object> response = new HashMap<>();
        Calendar calendar = Calendar.getInstance();
        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss");

        return webClientBuilder.build().get()
                .uri(urlGateway+"/api/movement/numberOfMovements?idCuenta="+idCuenta)
                .retrieve().bodyToMono(Long.class).flatMap(number->{
                    if (number <= amountOfMovements) {

                        return currentDao.findById(idCuenta).flatMap( c -> {

                            if(c.getAmountCurrent() - cantidad < 0) {
                                response.put("mensaje", "No puede realizar este retiro ya que no cuenta con el saldo suficiente");
                                return Mono.just(new ResponseEntity<Map<String,Object>>(response, HttpStatus.OK));
                            }else {

                                c.setAmountCurrent(c.getAmountCurrent() - cantidad);
                                return currentDao.save(c).flatMap(acc -> {

                                    Date date = Calendar.getInstance().getTime();
                                    MovementDocument movement = MovementDocument.builder()
                                            .tipoMovimiento("Retiro")
                                            .tipoProducto("Cuenta Corriente")
                                            .fechaMovimiento(dateFormat.format(date))
                                            .idCuenta(idCuenta)
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

                        return currentDao.findById(idCuenta).flatMap( c -> {
                            if(c.getAmountCurrent() - cantidad -comissionPerMovement< 0) {
                                response.put("mensaje", "No puede realizar este retiro ya que no cuenta con el saldo suficiente");
                                return Mono.just(new ResponseEntity<Map<String,Object>>(response, HttpStatus.OK));
                            }else {



                                c.setAmountCurrent(c.getAmountCurrent() - cantidad-comissionPerMovement);
                                return currentDao.save(c).flatMap(acc -> {

                                    Date date = Calendar.getInstance().getTime();
                                    MovementDocument movement = MovementDocument.builder()
                                            .tipoMovimiento("Retiro")
                                            .tipoProducto("Cuenta Corriente")
                                            .comission(comissionPerMovement)
                                            .fechaMovimiento(dateFormat.format(date))
                                            .idCuenta(idCuenta)
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
    public Mono<ResponseEntity<Map<String, Object>>> consultarSaldo(String idCliente) {
        Map<String, Object> response = new HashMap<>();

        return currentDao.findById(idCliente).flatMap( c -> {


            response.put("mensaje", "El saldo de la cuenta es: S/."+c.getAmountCurrent());
            return Mono.just(new ResponseEntity<Map<String,Object>>(response, HttpStatus.OK));

        }).defaultIfEmpty(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }
}
