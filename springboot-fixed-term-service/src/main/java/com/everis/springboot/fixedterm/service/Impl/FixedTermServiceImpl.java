package com.everis.springboot.fixedterm.service.Impl;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.everis.springboot.fixedterm.dao.FixedTermDao;
import com.everis.springboot.fixedterm.documents.FixedTermDocument;
import com.everis.springboot.fixedterm.documents.MovementDocument;
import com.everis.springboot.fixedterm.service.FixedTermService;

import reactor.core.publisher.Mono;

@Service
public class FixedTermServiceImpl implements FixedTermService {
	
	@Autowired
	private FixedTermDao fixedTermDao;
	
	@Autowired
	private WebClient.Builder webClientBuilder;

	@Override
	public Mono<FixedTermDocument> createAccount(FixedTermDocument document) {
		return fixedTermDao.save(document);
	}

	@Override
	public Mono<ResponseEntity<Map<String,Object>>> depositar(String idCuenta,Double cantidad) {
		Map<String, Object> response = new HashMap<>();
		Calendar calendar = Calendar.getInstance();
		DateFormat dateFormat = new SimpleDateFormat("dd-mm-yyyy hh:mm:ss");
	
		
		return fixedTermDao.findById(idCuenta).flatMap( c -> {
			c.setSaldo(c.getSaldo() + cantidad);
			
			if(calendar.get(Calendar.DAY_OF_MONTH) == c.getDiaRetiro()) {
				
				return fixedTermDao.save(c).flatMap(acc -> {
					Date date = Calendar.getInstance().getTime();
					MovementDocument movement = MovementDocument.builder()
							.tipoMovimiento("Deposito")
							.tipoProducto("Cuenta Plazo Fijo")
							.fechaMovimiento(dateFormat.format(date))
							.idCuenta(idCuenta)
							.idCliente(acc.getIdCliente())
							.build();
					
					webClientBuilder.build().post()
					.uri("http://localhost:8090/api/movement/saveMovement")
					.body(Mono.just(movement), MovementDocument.class)
					.retrieve().bodyToMono(MovementDocument.class).subscribe();
					
					
					response.put("mensaje", "Se hizo el deposito exitosamente");
					response.put("cuenta", acc);
					return Mono.just(new ResponseEntity<Map<String,Object>>(response, HttpStatus.OK));
				});
			}else {
				response.put("mensaje", "No puede depositar porque no es el dia establecido");
				return Mono.just(new ResponseEntity<Map<String,Object>>(response, HttpStatus.OK));
			}
		}).defaultIfEmpty(new ResponseEntity<>(HttpStatus.NOT_FOUND));
	}

	@Override
	public Mono<ResponseEntity<Map<String,Object>>> retirar(String idCuenta,Double cantidad) {
		Map<String, Object> response = new HashMap<>();
		Calendar calendar = Calendar.getInstance();
		DateFormat dateFormat = new SimpleDateFormat("dd-mm-yyyy hh:mm:ss");
		
		return fixedTermDao.findById(idCuenta).flatMap( c -> {
			
			if(calendar.get(Calendar.DAY_OF_MONTH) == c.getDiaRetiro()) {
				if(c.getSaldo() - cantidad < 0) {
					response.put("mensaje", "No puede realizar este retiro ya que no cuenta con el saldo suficiente");
					return Mono.just(new ResponseEntity<Map<String,Object>>(response, HttpStatus.OK));
				}else {
					
					
					
					c.setSaldo(c.getSaldo() - cantidad);
					return fixedTermDao.save(c).flatMap(acc -> {
						
						Date date = Calendar.getInstance().getTime();
						MovementDocument movement = MovementDocument.builder()
								.tipoMovimiento("Retiro")
								.tipoProducto("Cuenta Plazo Fijo")
								.fechaMovimiento(dateFormat.format(date))
								.idCuenta(idCuenta)
								.idCliente(acc.getIdCliente())
								.build();
						
						webClientBuilder.build().post()
						.uri("http://localhost:8090/api/movement/saveMovement")
						.body(Mono.just(movement), MovementDocument.class)
						.retrieve().bodyToMono(MovementDocument.class).subscribe();
						
						response.put("mensaje", "Se hizo el retiro exitosamente");
						response.put("cuenta", acc);
						return Mono.just(new ResponseEntity<Map<String,Object>>(response, HttpStatus.OK));
					});
				}
			}else {
				response.put("mensaje", "No puede retirar porque no es el dia establecido");
				return Mono.just(new ResponseEntity<Map<String,Object>>(response, HttpStatus.OK));
			}
		}).defaultIfEmpty(new ResponseEntity<>(HttpStatus.NOT_FOUND));
	}

	@Override
	public Mono<ResponseEntity<Map<String, Object>>> consultarSaldo(String idCliente) {
		Map<String, Object> response = new HashMap<>();
		
		return fixedTermDao.findByIdCliente(idCliente).flatMap( c -> {
			
			
			response.put("mensaje", "El saldo de la cuenta es: S/."+c.getSaldo());
			return Mono.just(new ResponseEntity<Map<String,Object>>(response, HttpStatus.OK));
			
		}).defaultIfEmpty(new ResponseEntity<>(HttpStatus.NOT_FOUND));
	}
	
	

}
