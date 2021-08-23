package com.everis.springboot.debits.documents;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.data.annotation.Id;

@AllArgsConstructor
@NoArgsConstructor
@Data
@ToString
public class CreatedAccountDocument {

    @Id
    private String id;

    private String account_type;

    private String idAccount;

    private String client;

    private Double mount;
}
