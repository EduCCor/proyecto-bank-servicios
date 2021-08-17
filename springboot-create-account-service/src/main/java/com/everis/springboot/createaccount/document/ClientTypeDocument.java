package com.everis.springboot.createaccount.document;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Document(collection = "clients_type")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ClientTypeDocument {
	@Id
	private String id;

	private String description;

}
