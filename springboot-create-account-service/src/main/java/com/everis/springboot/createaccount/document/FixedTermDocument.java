package com.everis.springboot.createaccount.document;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FixedTermDocument {
	
	private String id;
	
	private Double saldo;
	
	private String fechaCreacion;
	
	private String idCliente;
	
	private Integer diaRetiro;
}
