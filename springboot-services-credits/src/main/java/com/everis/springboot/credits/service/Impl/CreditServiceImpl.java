package com.everis.springboot.credits.service.Impl;

import com.everis.springboot.credits.dao.CreditDao;
import com.everis.springboot.credits.documents.ClientDocument;
import com.everis.springboot.credits.documents.CreditDocument;
import com.everis.springboot.credits.documents.MovementDocument;
import com.everis.springboot.credits.service.CreditService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class CreditServiceImpl implements CreditService {

    @Autowired
    private WebClient.Builder webClientBuilder;

    @Autowired
    private CreditDao creditDao;

    @Value("${everis.credits.url.gateway}")
    private String urlGateway;

    @Override
    public Mono<ResponseEntity<Map<String, Object>>> saveCredit(String idClient, CreditDocument account) {
        Map<String, Object> response = new HashMap<>();

        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");

        Mono<ClientDocument> client = webClientBuilder.build().get()
                .uri(urlGateway+"/api/client/client/" + idClient)
                .retrieve()
                .bodyToMono(ClientDocument.class);

        return creditDao.findByIdClient(idClient).collectList().flatMap( credits -> {

            Mono<ResponseEntity<Map<String,Object>>> res = client.flatMap(c -> {
                Integer creditAccount = 0;
                Integer creditCard = 0;
                String tipoCliente = "";

                System.out.println(c.toString());
                System.out.println(credits.toString());

                if(!account.getCreditType().equals("Credito")  && !account.getCreditType().equals("Tarjeta de Credito")) {
                    response.put("mensaje", "No se puede crear el tipo de credito, los tipos de creditos solo pueden ser: Credito y Tarjeta de Credito");
                    return Mono.just(new ResponseEntity<Map<String,Object>>(response,HttpStatus.BAD_REQUEST));
                }

                if(c.getClient_type().getDescription().equals("Personal") || c.getClient_type().getDescription().equals("VIP")) {
                    for (CreditDocument cre : credits) {
                        if(cre.getCreditType().equals("Credito Personal")) {
                            creditAccount++;
                        }
                        if(cre.getCreditType().equals("Tarjeta de Credito")) {
                            creditCard++;
                        }

                        if(account.getCreditType().equals("Credito") && creditAccount>0) {
                            response.put("mensaje", "No puede crear el credito, un cliente personal no puede tener más de un credito");
                            return Mono.just(new ResponseEntity<Map<String,Object>>(response,HttpStatus.BAD_REQUEST));
                        }
                        if(account.getCreditType().equals("Tarjeta de Credito") && creditCard>0) {
                            response.put("mensaje", "No puede crear la tarjeta, un cliente personal no puede tener más de una tarjeta de credito");
                            return Mono.just(new ResponseEntity<Map<String,Object>>(response,HttpStatus.BAD_REQUEST));
                        }
                    }

                }else if(c.getClient_type().getDescription().equals("Empresarial") || c.getClient_type().getDescription().equals("PYME")) {
                    for (CreditDocument cre : credits) {
                        if(cre.getCreditType().equals("Tarjeta de Credito")) {
                            creditCard++;
                        }
                        if(account.getCreditType().equals("Tarjeta de Credito") && creditCard>0) {
                            response.put("mensaje", "No puede crear la tarjeta, un cliente empresarial no puede tener más de una tarjeta de credito");
                            return Mono.just(new ResponseEntity<Map<String,Object>>(response,HttpStatus.BAD_REQUEST));
                        }
                    }
                }else if(!c.getClient_type().getDescription().equals("Empresarial") && !c.getClient_type().getDescription().equals("Personal") &&
                        !c.getClient_type().getDescription().equals("VIP") && !c.getClient_type().getDescription().equals("PYME")) {
                    response.put("mensaje", "El tipo de cliente es incorrecto");
                    return Mono.just(new ResponseEntity<Map<String,Object>>(response,HttpStatus.BAD_REQUEST));
                }

                if((c.getClient_type().getDescription().equals("Personal") || c.getClient_type().getDescription().equals("VIP")) && account.getCreditType().equals("Credito")) {
                    tipoCliente = "Credito Personal";
                }else if ((c.getClient_type().getDescription().equals("Empresarial") || c.getClient_type().getDescription().equals("PYME")) && account.getCreditType().equals("Credito")) {
                    tipoCliente = "Credito Empresarial";
                }else {
                    tipoCliente = account.getCreditType();
                }

                Date date = Calendar.getInstance().getTime();
                account.setIdClient(idClient);
                account.setCreationDate(dateFormat.format(date));
                account.setCreditLimit(account.getBalance());
                account.setCreditPaid(0.0);

                account.setCreditType(tipoCliente);

                return creditDao.save(account).flatMap( cre -> {
                    response.put("creditSaved", cre);
                    response.put("mensaje", "Credito registrada con exito");
                    return Mono.just(new ResponseEntity<Map<String,Object>>(response,HttpStatus.OK));
                });

            });


            return res;
        });
    }

    @Override
    public Mono<ResponseEntity<Map<String, Object>>> payCredit(String idCredit, Double cantidad) {
        Map<String, Object> response = new HashMap<>();
        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");

        return creditDao.findById(idCredit).flatMap( c -> {


            if(c.getCreditPaid() == c.getCreditLimit()) {
                response.put("mensaje", "Usted ya termino de pagar el credito");
                return Mono.just(new ResponseEntity<Map<String, Object>>(response, HttpStatus.OK));
            }else if( c.getCreditPaid() + cantidad > c.getCreditLimit()) {
                response.put("mensaje", "No puede pagar mas del limite establecido del credito");
                return Mono.just(new ResponseEntity<Map<String, Object>>(response, HttpStatus.OK));
            }else {
                c.setCreditPaid(c.getCreditPaid() + cantidad);

                return creditDao.save(c).flatMap(cre -> {
                    Date date = Calendar.getInstance().getTime();
                    MovementDocument movement = MovementDocument.builder()
                            .tipoMovimiento("Pago Credito")
                            .tipoProducto(cre.getCreditType())
                            .fechaMovimiento(dateFormat.format(date))
                            .idCuenta(idCredit)
                            .idCliente(cre.getIdClient())
                            .build();

                    webClientBuilder.build().post()
                            .uri(urlGateway+"/api/movement/saveMovement")
                            .body(Mono.just(movement), MovementDocument.class)
                            .retrieve().bodyToMono(MovementDocument.class).subscribe();


                    response.put("mensaje", "Se hizo el pago del credito correctamente");
                    response.put("credito", cre);
                    return Mono.just(new ResponseEntity<Map<String,Object>>(response, HttpStatus.OK));
                });
            }
        }).defaultIfEmpty(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @Override
    public Mono<ResponseEntity<Map<String, Object>>> spendCredit(String idCredit, Double cantidad) {
        Map<String, Object> response = new HashMap<>();
        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");

        return creditDao.findById(idCredit).flatMap( c -> {
            if(c.getBalance() - cantidad < 0) {
                response.put("mensaje", "No puede gastar más del crédito dado");
                return Mono.just(new ResponseEntity<Map<String, Object>>(response, HttpStatus.OK));
            }else {
                c.setBalance(c.getBalance() - cantidad);

                return creditDao.save(c).flatMap(cre -> {
                    Date date = Calendar.getInstance().getTime();
                    MovementDocument movement = MovementDocument.builder()
                            .tipoMovimiento("Consumo Credito")
                            .tipoProducto(cre.getCreditType())
                            .fechaMovimiento(dateFormat.format(date))
                            .idCuenta(idCredit)
                            .idCliente(cre.getIdClient())
                            .build();

                    webClientBuilder.build().post()
                            .uri(urlGateway+"/api/movement/saveMovement")
                            .body(Mono.just(movement), MovementDocument.class)
                            .retrieve().bodyToMono(MovementDocument.class).subscribe();


                    response.put("mensaje", "Se hizo el gasto del credito correctamente");
                    response.put("credito", cre);
                    return Mono.just(new ResponseEntity<Map<String,Object>>(response, HttpStatus.OK));
                });
            }
        }).defaultIfEmpty(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @Override
    public Mono<ResponseEntity<Map<String, Object>>> consultCredit(String idCredit) {
        Map<String, Object> response = new HashMap<>();

        return creditDao.findById(idCredit).flatMap( c -> {


            response.put("mensaje", "El saldo del credito es: S/."+c.getBalance());
            return Mono.just(new ResponseEntity<Map<String,Object>>(response, HttpStatus.OK));

        }).defaultIfEmpty(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @Override
    public Mono<ResponseEntity<Map<String, Object>>> getOnlyCredits(String idClient) {
        Map<String, Object> response = new HashMap<>();

        return creditDao.findByIdClientAndCreditType(idClient, "Credito Personal").flatMap( c -> {
            response.put("credito", c);
            return Mono.just(new ResponseEntity<>(response, HttpStatus.OK));
        });
    }

    @Override
    public Mono<ResponseEntity<Map<String, Object>>> getCreditCards(String idClient) {
        Map<String, Object> response = new HashMap<>();
        return creditDao.findByIdClientAndCreditType(idClient, "Tarjeta de Credito").flatMap( c -> {
            response.put("credito", c);
            return Mono.just(new ResponseEntity<>(response, HttpStatus.OK));
        });
    }
}
