package com.everis.springboot.createaccount.document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class CreditDocument {

    private String id;

    private String typeCredit;

    private double balance;

    private double limitCredit;

    private String idClient;

    private String createCredit;

    private Double paidCredit;
}
