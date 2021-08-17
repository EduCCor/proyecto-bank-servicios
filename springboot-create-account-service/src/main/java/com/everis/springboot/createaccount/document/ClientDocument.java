package com.everis.springboot.createaccount.document;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ClientDocument {

	private String id;
	
	private String first_name;
	
	private String last_name;
	
	private ClientTypeDocument client_type;
	
	public ClientDocument(String first_name, String last_name, ClientTypeDocument client_type) {
		this.first_name = first_name;
		this.last_name = last_name;
		this.client_type = client_type;
	}

}
