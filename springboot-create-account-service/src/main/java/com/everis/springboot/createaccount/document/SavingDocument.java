package com.everis.springboot.createaccount.document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class SavingDocument {

    private String id;

    private double amountSaving;

    private String idClient;

    private int movMonthSaving;

    private String type;

    private Date createSaving;
}
