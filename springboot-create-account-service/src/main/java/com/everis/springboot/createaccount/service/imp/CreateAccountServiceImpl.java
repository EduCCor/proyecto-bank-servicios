package com.everis.springboot.createaccount.service.imp;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

import com.everis.springboot.createaccount.document.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.everis.springboot.createaccount.dao.CreateAccountDao;
import com.everis.springboot.createaccount.service.CreateAccountService;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Service
@Slf4j
public class CreateAccountServiceImpl implements CreateAccountService {
	
	
	@Autowired
	private WebClient.Builder webClientBuilder;
	
	@Autowired
	private CreateAccountDao createAccountDao;

	private static final Logger LOGGER = LoggerFactory.getLogger(CreateAccountServiceImpl.class);

	@Value("${everis.create.precio.mantenimiento}")
	private double costOfMaintenment;

	@Value("${everis.create.cantidad.movimientos}")
	private Integer movMonth;

	@Value("${everis.create.dia-retiro.plazo-fijo}")
	private Integer diaRetiro;

	@Value("${everis.url.gateway}")
	private String urlGateway;

	@Override
	public Mono<CreateAccountDocument> findAccountsById(String id) {
		LOGGER.info("Search Account by ID");
		return createAccountDao.findById(id).doOnNext(c -> {
			LOGGER.info("Account: " + c.getAccount_type() + " Client: " + c.getClient() + " Found!");
		});
	}

	@Override
	public Mono<ResponseEntity<Map<String, Object>>> saveAccount(String id, CreateAccountDocument account) {
		Map<String, Object> response = new HashMap<>();
		LOGGER.info("In Validation for Create ACcount");
		
		DateFormat dateFormat = new SimpleDateFormat("dd-mm-yyyy hh:mm:ss");
		
		Mono<ClientDocument> client = webClientBuilder.build().get()
				.uri(urlGateway + "/api/client/client/" + id)
				.retrieve()
				.bodyToMono(ClientDocument.class);
		
		
		return createAccountDao.findByClient(id).collectList().flatMap( accounts -> {
			
			Mono<ResponseEntity<Map<String,Object>>> res = client.flatMap(c -> {
				Integer cAhorro = 0;
				Integer cCorriente = 0;
				Integer cPlazoFijo = 0;
				
//				System.out.println(c.toString());
				LOGGER.info("First Validation");

				if(!Arrays.asList("Cuenta de Ahorro", "Cuenta Corriente", "Cuenta Plazo Fijo").contains(account.getAccount_type())){
					response.put("mensaje", "No se puede crear el tipo de cuenta, comuniquese con el Administrador");
					LOGGER.info("No se puede crear el tipo de cuenta, comuniquese con el Administrador");
					return Mono.just(new ResponseEntity<Map<String,Object>>(response,HttpStatus.BAD_REQUEST));
				}

				if(Arrays.asList("Personal", "VIP").contains(c.getClient_type().getDescription())) {
					for (CreateAccountDocument acc : accounts) {
						if(acc.getAccount_type().equals("Cuenta de Ahorro")) {
							cAhorro++;
						}
						if(acc.getAccount_type().equals("Cuenta Corriente")) {
							cCorriente++;
						}
						if(acc.getAccount_type().equals("Cuenta Plazo Fijo")) {
							cPlazoFijo++;
						}
						if(account.getAccount_type().equals("Cuenta de Ahorro") && cAhorro>0) {
							response.put("mensaje", "No puede crear la cuenta, un cliente no puede tener más de una cuenta de ahorro");
							LOGGER.info("No puede crear la cuenta, un cliente no puede tener más de una cuenta de ahorro");
							return Mono.just(new ResponseEntity<Map<String,Object>>(response,HttpStatus.BAD_REQUEST));
						}
						if(account.getAccount_type().equals("Cuenta Corriente") && cCorriente>0) {
							response.put("mensaje", "No puede crear la cuenta, un cliente no puede tener más de una cuenta corriente");
							LOGGER.info("No puede crear la cuenta, un cliente no puede tener más de una cuenta corriente");
							return Mono.just(new ResponseEntity<Map<String,Object>>(response,HttpStatus.BAD_REQUEST));
						}
						if(account.getAccount_type().equals("Cuenta Plazo Fijo") && cPlazoFijo>0) {
							response.put("mensaje", "No puede crear la cuenta, un cliente no puede tener más de una cuenta a plazo fijo");
							LOGGER.info("No puede crear la cuenta, un cliente no puede tener más de una cuenta a plazo fijo");
							return Mono.just(new ResponseEntity<Map<String,Object>>(response,HttpStatus.BAD_REQUEST));
						}
					}
					
				}else if(Arrays.asList("Empresarial", "PYME").contains(c.getClient_type().getDescription())) {
					if(account.getAccount_type().equals("Cuenta de Ahorro")) {
						response.put("mensaje", "Un usuario empresarial no puede tener cuenta de ahorro");
						LOGGER.info("Un usuario empresarial no puede tener cuenta de ahorro");
						return Mono.just(new ResponseEntity<Map<String,Object>>(response,HttpStatus.BAD_REQUEST));
					}
					if(account.getAccount_type().equals("Cuenta Plazo Fijo")) {
						response.put("mensaje", "Un usuario empresarial no puede tener cuenta a plazo fijo");
						LOGGER.info("Un usuario empresarial no puede tener cuenta a plazo fijo");
						return Mono.just(new ResponseEntity<Map<String,Object>>(response,HttpStatus.BAD_REQUEST));
					}
				}else if(!Arrays.asList("Empresarial", "Personal", "VIP", "PYME").contains(c.getClient_type().getDescription())) {
					response.put("mensaje", "Ingreso un tipo de cliente incorrecto");
					LOGGER.info("Ingreso un tipo de cliente incorrecto");
					return Mono.just(new ResponseEntity<Map<String,Object>>(response,HttpStatus.BAD_REQUEST));
				}

				LOGGER.info("Second Validation");
				account.setClient(id);
				
				if(account.getAccount_type().equals("Cuenta Plazo Fijo")) {
					
					Date date = Calendar.getInstance().getTime();
					FixedTermDocument fixedTerm = FixedTermDocument.builder()
							.saldo(account.getMount())
							.fechaCreacion(dateFormat.format(date))
							.idCliente(id)
							.diaRetiro(diaRetiro)
							.build();
					
					
					webClientBuilder.build().post()
					.uri(urlGateway + "/api/fixed-term/saveAccount")
					.body(Mono.just(fixedTerm), FixedTermDocument.class)
					.retrieve().bodyToMono(FixedTermDocument.class).subscribe();

				}else if(account.getAccount_type().equals("Cuenta Corriente")){

					Date date = Calendar.getInstance().getTime();

					if(c.getClient_type().getDescription().equals("PYME")){
						LOGGER.info("Cliente PYME abrira una Cuenta Corriente");
						Map<String, Object> responseEmpty = new HashMap<>();
						responseEmpty.put("mensaje", "No existen tarjetas de credito para este usuario");
						LOGGER.info("No existen tarjetas de credito para este usuario");

						return webClientBuilder.build().get()
								.uri(urlGateway+"/api/credit/getCreditCards/"+id)
								.retrieve().bodyToMono(CreditDocument.class).flatMap(cre -> {
									CurrentDocument currentDocument = CurrentDocument.builder()
											.amountCurrent(account.getMount())
											.costCurrent(costOfMaintenment)
											.createCurrent(date)
											.idClient(id)
											.build();

									return	webClientBuilder.build().post()
											.uri(urlGateway+"/api/currentAccount")
											.body(Mono.just(currentDocument), CurrentDocument.class)
											.retrieve().bodyToMono(CurrentDocument.class).flatMap(createdAccount -> {
												account.setIdAccount(createdAccount.getId());

												return createAccountDao.save(account).flatMap( p -> {

													response.put("productSaved", p);
													response.put("mensaje", "Cuenta registrada con exito");
													LOGGER.info("Cuenta registrada con exito");
													return Mono.just(new ResponseEntity<Map<String,Object>>(response,HttpStatus.OK));

												});
											});

								}).defaultIfEmpty(new ResponseEntity<>(responseEmpty, HttpStatus.BAD_GATEWAY));

					}else{
						CurrentDocument currentDocument = CurrentDocument.builder()
								.amountCurrent(account.getMount())
								.costCurrent(costOfMaintenment)
								.createCurrent(date)
								.idClient(id)
								.build();

						return	webClientBuilder.build().post()
								.uri(urlGateway+"/api/currentAccount")
								.body(Mono.just(currentDocument), CurrentDocument.class)
								.retrieve().bodyToMono(CurrentDocument.class).flatMap(createdAccount -> {
									account.setIdAccount(createdAccount.getId());

									return createAccountDao.save(account).flatMap( p -> {

										response.put("productSaved", p);
										response.put("mensaje", "Cuenta registrada con exito");
										LOGGER.info("Cuenta registrada con exito");
										return Mono.just(new ResponseEntity<Map<String,Object>>(response,HttpStatus.OK));

									});
								});
					}
				}else if(account.getAccount_type().equals("Cuenta de Ahorro")){

					Date date = Calendar.getInstance().getTime();

					if(c.getClient_type().getDescription().equals("VIP")){
						LOGGER.info("Cliente vIP abrira una Cuenta de Ahorro");
						Map<String, Object> responseEmpty = new HashMap<>();
						responseEmpty.put("mensaje", "No existen tarjetas de credito para este usuario");
						LOGGER.info("No existen tarjetas de credito para este usuario");
						return webClientBuilder.build().get()
								.uri(urlGateway+"/api/credit/getCreditCards/"+id)
								.retrieve().bodyToMono(CreditDocument.class).flatMap( cre ->{
									SavingDocument savingDocument = SavingDocument.builder()
											.amountSaving(account.getMount())
											.createSaving(date)
											.idClient(id)
											.movMonthSaving(movMonth)
											.build();

									return	webClientBuilder.build().post()
											.uri(urlGateway+"/api/accountSavings")
											.body(Mono.just(savingDocument), SavingDocument.class)
											.retrieve().bodyToMono(SavingDocument.class).flatMap(createdAccount -> {
												account.setIdAccount(createdAccount.getId());
												return createAccountDao.save(account).flatMap( p -> {

													response.put("productSaved", p);
													response.put("mensaje", "Cuenta registrada con exito");
													LOGGER.info("Cuenta registrada con exito");
													return Mono.just(new ResponseEntity<Map<String,Object>>(response,HttpStatus.OK));

												});
											});

								}).defaultIfEmpty(new ResponseEntity<>(responseEmpty, HttpStatus.BAD_GATEWAY));
					}else{
						SavingDocument savingDocument = SavingDocument.builder()
								.amountSaving(account.getMount())
								.createSaving(date)
								.idClient(id)
								.movMonthSaving(movMonth)
								.build();


						return	webClientBuilder.build().post()
								.uri(urlGateway+"/api/accountSavings")
								.body(Mono.just(savingDocument), SavingDocument.class)
								.retrieve().bodyToMono(SavingDocument.class).flatMap(createdAccount -> {
									account.setIdAccount(createdAccount.getId());
									return createAccountDao.save(account).flatMap( p -> {

										response.put("productSaved", p);
										response.put("mensaje", "Cuenta registrada con exito");
										LOGGER.info("Cuenta registrada con exito");
										return Mono.just(new ResponseEntity<Map<String,Object>>(response,HttpStatus.OK));

									});
								});
					}

				}
				
				return createAccountDao.save(account).flatMap( p -> {
					response.put("productSaved", p);
					response.put("mensaje", "Cuenta registrada con exito");
					LOGGER.info("Cuenta registrada con exito");

					return Mono.just(new ResponseEntity<Map<String,Object>>(response,HttpStatus.OK)); 
				});
				
			});

			return res;
		});
	}

}
