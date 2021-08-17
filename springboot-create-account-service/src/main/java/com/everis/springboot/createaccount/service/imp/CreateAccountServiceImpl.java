package com.everis.springboot.createaccount.service.imp;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.everis.springboot.createaccount.dao.CreateAccountDao;
import com.everis.springboot.createaccount.document.ClientDocument;
import com.everis.springboot.createaccount.document.CreateAccountDocument;
import com.everis.springboot.createaccount.document.FixedTermDocument;
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
	
	@Value("${everis.dia-retiro.plazo-fijo}")
	private Integer diaRetiro;

	@Override
	public Mono<CreateAccountDocument> findAccountsById(String id) {
		return createAccountDao.findById(id);
	}

	@Override
	public Mono<ResponseEntity<Map<String, Object>>> saveAccount(String id, CreateAccountDocument account) {
		Map<String, Object> response = new HashMap<>();
		
		DateFormat dateFormat = new SimpleDateFormat("dd-mm-yyyy hh:mm:ss");
		
		Mono<ClientDocument> client = webClientBuilder.build().get()
				.uri("http://localhost:8090/api/client/client/"+id)
//				.uri("http://localhost:55457/client/"+id)
				.retrieve()
				.bodyToMono(ClientDocument.class);
		
		
		return createAccountDao.findByClient(id).collectList().flatMap( accounts -> {
			
			Mono<ResponseEntity<Map<String,Object>>> res = client.flatMap(c -> {
				Integer cAhorro = 0;
				Integer cCorriente = 0;
				Integer cPlazoFijo = 0;
				
				System.out.println(c.toString());
				
				if(c.getClient_type().getDescription().equals("Personal") ) {
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
							return Mono.just(new ResponseEntity<Map<String,Object>>(response,HttpStatus.BAD_REQUEST));
						}
						if(account.getAccount_type().equals("Cuenta Corriente") && cCorriente>0) {
							response.put("mensaje", "No puede crear la cuenta, un cliente no puede tener más de una cuenta corriente");
							return Mono.just(new ResponseEntity<Map<String,Object>>(response,HttpStatus.BAD_REQUEST));
						}
						if(account.getAccount_type().equals("Cuenta Plazo Fijo") && cPlazoFijo>0) {
							response.put("mensaje", "No puede crear la cuenta, un cliente no puede tener más de una cuenta a plazo fijo");
							return Mono.just(new ResponseEntity<Map<String,Object>>(response,HttpStatus.BAD_REQUEST));
						}
					}
					
				}else if(c.getClient_type().getDescription().equals("Empresarial")) {
					if(account.getAccount_type().equals("Cuenta de Ahorro")) {
						response.put("mensaje", "Un usuario empresarial no puede tener cuenta de ahorro");
						return Mono.just(new ResponseEntity<Map<String,Object>>(response,HttpStatus.BAD_REQUEST));
					}
					if(account.getAccount_type().equals("Cuenta Plazo Fijo")) {
						response.put("mensaje", "Un usuario empresarial no puede tener cuenta a plazo fijo");
						return Mono.just(new ResponseEntity<Map<String,Object>>(response,HttpStatus.BAD_REQUEST));
					}
				}else if(!c.getClient_type().getDescription().equals("Empresarial") && !c.getClient_type().getDescription().equals("Personal")) {
					response.put("mensaje", "Ingreso un tipo de cliente incorrecto");
					return Mono.just(new ResponseEntity<Map<String,Object>>(response,HttpStatus.BAD_REQUEST));
				}
				
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
					.uri("http://localhost:8090/api/fixed-term/saveAccount")
					.body(Mono.just(fixedTerm), FixedTermDocument.class)
					.retrieve().bodyToMono(FixedTermDocument.class).subscribe();
				}
				
				return createAccountDao.save(account).flatMap( p -> {
					response.put("productSaved", p);
					response.put("mensaje", "Cuenta registrada con exito");
					return Mono.just(new ResponseEntity<Map<String,Object>>(response,HttpStatus.OK)); 
				});
				
			});
			
			
			return res;
		});
	}

}
