package com.everis.springboot.movements.documents;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "movements")
public class MovementDocument {
	
	@Id
	private String id;
	
	private String tipoMovimiento;
	
	private String tipoProducto;
	
	private String fechaMovimiento;
	
	private String idCuenta;
	
	private String idCliente;
	
}
