package com.everis.springboot.saving.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Document(collection = "saving")
public class SavingDocument {

    @Id
    private String id;

    private int limitMov;

    private String commission;

    private String client;

    private String product;
}
