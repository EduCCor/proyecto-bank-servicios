package com.everis.springboot.debits.documents;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class AccountDocument {

    private String idAccount;
    private String typeAccount;
    private Boolean principal;
    private Date timeAdded;
}
