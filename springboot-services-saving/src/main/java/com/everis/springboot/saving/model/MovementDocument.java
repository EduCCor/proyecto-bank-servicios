package com.everis.springboot.saving.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class MovementDocument {

    private String id;

    private String tipoMovimiento;

    private String tipoProducto;

    private Date fechaMovimiento;

    private double comission;

    private String idCuenta;

    private String idCliente;
}
