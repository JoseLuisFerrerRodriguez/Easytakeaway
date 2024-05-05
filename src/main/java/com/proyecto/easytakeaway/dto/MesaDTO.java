/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.proyecto.easytakeaway.dto;

import com.proyecto.easytakeaway.modelos.Mesa;
import com.proyecto.easytakeaway.modelos.Pedido;
import jakarta.persistence.*;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.Collection;

/**
 *
 * @author Jose
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MesaDTO {

    private Integer id;

    @NotNull
    @Digits(message="{admin.mesas.validacion.tiponumero}", fraction = 0, integer = 4)
    @Min(value=1, message="{admin.mesas.validacion.numero}")
    @Max(value=1000, message="{admin.mesas.validacion.numero}")
    private Integer numeroMesa;

    @NotNull
    @Digits(message="{admin.mesas.validacion.tiponumero}", fraction = 0, integer = 4)
    @Min(value=1, message="{admin.mesas.validacion.numero}")
    @Max(value=1000, message="{admin.mesas.validacion.capacidad}")
    private Integer capacidad;

    private String posicion;

    private String imagenQR;

    private Boolean esNueva;

    public Mesa convertirDTOaModelo(){
        Mesa mesa = new Mesa();

        mesa.setId(this.getId());
        mesa.setNumeroMesa(this.getNumeroMesa());
        mesa.setCapacidad(this.getCapacidad());
        mesa.setPosicion(this.getPosicion());
        mesa.setImagenQR(this.getImagenQR());

        return mesa;
    }


    
}
