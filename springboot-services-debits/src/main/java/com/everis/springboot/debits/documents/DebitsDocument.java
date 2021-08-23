package com.everis.springboot.debits.documents;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Document(collection = "debits")
public class DebitsDocument {

    @Id
    private String id;

    private String numberCard;

    private Date createDebits;

    private Date modifiedDebits;

    private String idClient;

    private List<AccountDocument> multiAccounts;
}
