package com.everis.springboot.current.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ClientModel {

    private String id;
    private String name;
    private TypeClientModel type;
    private Date createAt;
}
