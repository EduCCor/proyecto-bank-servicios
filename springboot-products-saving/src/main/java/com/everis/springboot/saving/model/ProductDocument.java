package com.everis.springboot.saving.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ProductDocument {

    @Id
    private String id;

    private String account_type;

    private String client;

    private Double mount;
}
