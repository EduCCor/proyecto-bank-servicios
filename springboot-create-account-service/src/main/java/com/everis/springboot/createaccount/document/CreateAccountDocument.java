package com.everis.springboot.createaccount.document;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Document(collection = "accounts")
public class CreateAccountDocument {
	
	@Id
	private String id;
	
	private String account_type;
	
	private String client;
	
	private Double mount;
	
}
