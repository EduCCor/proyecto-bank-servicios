package com.everis.springboot.current.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Document(collection="cuentaCorriente")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CurrentDocument {

    @Id
    private String id;

    private double amountCurrent;

    private String idClient;

    private double costCurrent;

    private String type;

    private Date createCurrent;

    private Date modifiedCurrent;
}
