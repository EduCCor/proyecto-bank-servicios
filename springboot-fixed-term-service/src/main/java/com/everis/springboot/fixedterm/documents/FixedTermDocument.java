package com.everis.springboot.fixedterm.documents;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "fixed_terms")
public class FixedTermDocument {
	
	@Id
	private String id;
	
	private Double saldo;
	
	private String fechaCreacion;
	
	private String idCliente;
	
	private Integer diaRetiro;
}
