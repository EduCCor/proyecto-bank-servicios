package com.everis.springboot.saving.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Document(collection = "saving")
public class SavingDocument {

    @Id
    private String id;

    private double amountSaving;

    private String idClient;

    private int movMonthSaving;

    private String type;

    private Date createSaving;

    private Date modifiedSaving;
}
