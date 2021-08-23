package com.everis.springboot.debits.service.Impl;

import com.everis.springboot.debits.dao.DebitsDao;
import com.everis.springboot.debits.documents.AccountDocument;
import com.everis.springboot.debits.documents.CreatedAccountDocument;
import com.everis.springboot.debits.documents.DebitsDocument;
import com.everis.springboot.debits.service.DebitsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.*;
import java.util.stream.Collectors;

public class DebitsServiceImpl implements DebitsService {

    public static final Logger LOGGER = LoggerFactory.getLogger(DebitsServiceImpl.class);

    @Autowired
    private WebClient.Builder webClientBuilder;

    @Autowired
    private DebitsDao debitsDao;

    @Value("${everis.debits.url.gateway}")
    private String urlGateway;

    @Override
    public Mono<DebitsDocument> createDebit(String idClient, String numberCard) {
        DebitsDocument debitsDocument = DebitsDocument.builder()
                .numberCard(numberCard)
                .createDebits(new Date())
                .idClient(idClient)
                .multiAccounts(new ArrayList<>())
                .build();

        return debitsDao.save(debitsDocument);
    }

    @Override
    public Mono<ResponseEntity<Map<String, Object>>> accountsDebit(String idDebitCard, AccountDocument account) {
        Map<String, Object> response = new HashMap<>();

        if(!Arrays.asList("Cuenta de Ahorro", "Cuenta Corriente", "Cuenta Plazo Fijo").contains(account.getTypeAccount())){
            response.put("mensaje", "El tipo de cuenta a asociar es incorrecto");
            LOGGER.info("El tipo de cuenta a asociar es incorrecto");
            return Mono.just(new ResponseEntity<>(response, HttpStatus.BAD_REQUEST));
        }

        String urlAccount = account.getTypeAccount().equals("Cuenta Corriente") ?
                "currentAccount" : account.getTypeAccount().equals("Cuenta de Ahorro") ?
                "accountSavings" : account.getTypeAccount().equals("Cuenta Plazo Fijo") ?
                "fixed-term" : "";

        Map<String, Object> responseIfEmptyAccount = new HashMap<>();
        responseIfEmptyAccount.put("mensaje", "cuenta ingresada incorrecta, 0 registros");

        Map<String, Object> responseIfEmptyCredit = new HashMap<>();
        responseIfEmptyCredit.put("mensaje", "La tarjeta de debito es incorrecta, 0 registros");

        return webClientBuilder.build().get()
                .uri(urlGateway + "/api/" + urlAccount + "/getAccount/" + account.getIdAccount())
                .retrieve()
                .bodyToMono(CreatedAccountDocument.class)
                .flatMap(c -> {
                    return debitsDao.findById(idDebitCard)
                            .flatMap(cc -> {
                                if(cc.getIdClient().equals(c.getClient())){
                                    response.put("mensaje", "No puede asignar una tarjeta de debito si no es la suya");
                                    LOGGER.info("No puede asignar una tarjeta de debito si no es la suya");
                                    return Mono.just(new ResponseEntity<>(response, HttpStatus.BAD_REQUEST));
                                }else{
                                    Integer acc = cc.getMultiAccounts().stream()
                                            .filter(a -> {
                                                return a.getIdAccount().equals(account.getIdAccount());
                                            })
                                            .collect(Collectors.toList()).size();

                                    if(acc > 0){
                                        response.put("mensaje", "esta cuenta ya se encuentra asociada a esta tarjeta de debito");
                                        LOGGER.info("esta cuenta ya se encuentra asociada a esta tarjeta de debito");
                                        return Mono.just(new ResponseEntity<>(response, HttpStatus.BAD_REQUEST));
                                    }else{
                                        account.setTimeAdded(new Date());

                                        boolean princ = cc.getMultiAccounts().size() == 0 ? true : false;

                                        cc.getMultiAccounts().add(account);

                                        return debitsDao.save(cc).flatMap(ccc -> {
                                            response.put("mensaje", "Se asocio y registro la cuenta con exito a la tarjeta de debito");
                                            response.put("debit", ccc);
                                            LOGGER.info("Se asocio y registro la cuenta con exito a la tarjeta de debito");
                                            return Mono.just(new ResponseEntity<>(response, HttpStatus.OK));
                                        });

                                    }
                                }
                            }).defaultIfEmpty(new ResponseEntity<>(responseIfEmptyCredit, HttpStatus.NOT_FOUND));
                }).defaultIfEmpty(new ResponseEntity<>(responseIfEmptyAccount, HttpStatus.NOT_FOUND));
    }

    @Override
    public Mono<ResponseEntity<Map<String, Object>>> payWithAccount(String idDebitCard, Double amount) {
        Map<String, Object> response = new HashMap<>();

        return debitsDao.findById(idDebitCard)
                .flatMap(c -> {
                    if(c.getMultiAccounts().size() == 0){
                        response.put("mensaje", "No existen cuentas para la tarjeta de debito");
                        LOGGER.info("No existen cuentas para la tarjeta de debito");
                        return Mono.just(new ResponseEntity<>(response, HttpStatus.BAD_REQUEST));
                    }else{
                        AccountDocument accountDocument = c.getMultiAccounts().stream()
                                .filter(cc -> {
                                    return cc.getPrincipal() == true;
                                })
                                .collect(Collectors.toList())
                                .get(0);

                        String urlAccount = accountDocument.getTypeAccount().equals("Cuenta Corriente") ?
                                "currentAccount" : accountDocument.getTypeAccount().equals("Cuenta de Ahorro") ?
                                "accountSavings" : accountDocument.getTypeAccount().equals("Cuenta Plazo Fijo") ?
                                "fixed-term" : "";

                        return webClientBuilder.build().get()
                                .uri(urlGateway + "/api/" + urlAccount + "/payWithDebit/" + accountDocument.getIdAccount() + "/" + amount)
                                .retrieve()
                                .bodyToMono(Boolean.class)
                                .flatMap(ccc -> {
                                    if(ccc){
                                        response.put("mensaje", "se ralizo el pago exitosamente");
                                        LOGGER.info("se ralizo el pago exitosamente");
                                        return Mono.just(new ResponseEntity<>(response, HttpStatus.OK));
                                    }else{
                                        List<AccountDocument> secondAccount = c.getMultiAccounts()
                                                .stream()
                                                .filter(acc -> {
                                                    return acc.getPrincipal() == false;
                                                })
                                                .collect(Collectors.toList());

                                        Collections.sort(secondAccount, new Comparator<AccountDocument>() {
                                            @Override
                                            public int compare(AccountDocument o1, AccountDocument o2) {
                                                return o1.getTimeAdded().compareTo(o2.getTimeAdded());
                                            }
                                        });

                                        return getResponsePay(secondAccount, 0, amount);
                                    }
                                });

                    }
                });
    }

    public Mono<ResponseEntity<Map<String, Object>>> getResponsePay(List<AccountDocument> secondAccounts,Integer position, Double amount){
        Map<String, Object> response = new HashMap<>();

        if(secondAccounts.size() < position + 1){
            response.put("mensaje", "No existen cuentas para realizar el pago");
            LOGGER.info("No existen cuentas para realizar el pago");
            return Mono.just(new ResponseEntity<>(response, HttpStatus.OK));
        }else{

            String urlAccount = secondAccounts.get(position).getTypeAccount().equals("Cuenta Corriente") ?
                    "currentAccount" : secondAccounts.get(position).getTypeAccount().equals("Cuenta de Ahorro") ?
                    "accountSavings" : secondAccounts.get(position).getTypeAccount().equals("Cuenta Plazo Fijo") ?
                    "fixed-term" : "";

            return webClientBuilder.build().get()
                    .uri(urlGateway + "/api/" + urlAccount + "/payWithDebit/" + secondAccounts.get(position).getIdAccount() + "/" + amount)
                    .retrieve()
                    .bodyToMono(Boolean.class)
                    .flatMap(c -> {
                        if(c){
                            response.put("mensaje", "se realizo el pago exitosamente");
                            LOGGER.info("se realizo el pago exitosamente");
                            return Mono.just(new ResponseEntity<>(response, HttpStatus.OK));
                        }else {
                            Integer pos = position + 1;
                            return getResponsePay(secondAccounts, pos, amount);
                        }
                    });
        }
    }

    @Override
    public Mono<ResponseEntity<Map<String, Object>>> searchBalanceDebit(String idDebitCard) {
        Map<String, Object> response = new HashMap<>();

        return debitsDao.findById(idDebitCard)
                .flatMap(debit -> {
                    AccountDocument accountDocument = debit.getMultiAccounts()
                            .stream()
                            .filter(c -> {
                                return c.getPrincipal() == true;
                            })
                            .collect(Collectors.toList())
                            .get(0);

                    String urlAccount = accountDocument.getTypeAccount().equals("Cuenta Corriente") ?
                            "currentAccount" : accountDocument.getTypeAccount().equals("Cuenta de Ahorro") ?
                            "accountSavings" : accountDocument.getTypeAccount().equals("Cuenta Plazo Fijo") ?
                            "fixed-term" : "";

                    return webClientBuilder.build().get()
                            .uri(urlGateway + "/api/" + urlAccount + "/getBalance/" + accountDocument.getIdAccount())
                            .retrieve()
                            .bodyToMono(String.class)
                            .flatMap(cc -> {
                                response.put("mensaje", cc);
                                LOGGER.info(cc);
                                return Mono.just(new ResponseEntity<Map<String, Object>>(response, HttpStatus.OK));
                            });
                });
    }
}
