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
public class CurrentDocument {

    private String id;

    private double amountCurrent;

    private String idClient;

    private double costCurrent;

    private String type;

    private Date createCurrent;
}
