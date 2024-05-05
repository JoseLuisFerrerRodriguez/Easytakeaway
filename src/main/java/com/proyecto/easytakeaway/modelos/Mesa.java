/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.proyecto.easytakeaway.modelos;

import java.util.Collection;

import com.proyecto.easytakeaway.dto.MesaDTO;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *
 * @author Jose
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "mesas")
public class Mesa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MesaID")
    private Integer id;

    @Column(name = "numero")
    @NotNull
    private Integer numeroMesa;

    @Column(name = "capacidad")
    @NotNull
    private Integer capacidad;

    @Column(name = "posicion")
    private String posicion;

    @Column(name = "imagenqr")
    private String imagenQR;

    @OneToMany(mappedBy = "mesaID", fetch = FetchType.LAZY)
    private Collection<Pedido> pedidos;


    public MesaDTO convertirModeloaDTO(){
        MesaDTO mesaDTO = new MesaDTO();

        mesaDTO.setId(this.getId());
        mesaDTO.setNumeroMesa(this.getNumeroMesa());
        mesaDTO.setCapacidad(this.getCapacidad());
        mesaDTO.setPosicion(this.getPosicion());
        mesaDTO.setImagenQR(this.getImagenQR());

        return mesaDTO;
    }
    
}
