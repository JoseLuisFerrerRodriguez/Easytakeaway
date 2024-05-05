/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.proyecto.easytakeaway.dto;

import com.proyecto.easytakeaway.modelos.Producto;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import java.math.BigDecimal;

/**
 *
 * @author Jose
 */

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductoDTO {

    private Integer id;

    @NotNull
    @NotBlank
    @Size(max = 100)
    private String nombre;

    private String nombreCorto;

    private String alias;

    @Size(max = 255)
    private String descripcion;

    @NotNull
    @Column(name = "precio")
    private float precio;

    @NotNull
    private float iva;

    private String imagenURL;

    private CategoriaDTO categoria;

    public Producto convertirDTOaModelo() {
        Producto producto = new Producto();
        producto.setId(this.getId());
        producto.setNombre(this.getNombre());
        producto.setAlias(this.getAlias());
        producto.setDescripcion(this.getDescripcion());
        producto.setImagenURL(this.getImagenURL());
        producto.setPrecio(this.getPrecio());
        producto.setIva(this.getIva());
        producto.setCategoria(this.getCategoria().convertirDTOaModelo());

        return producto;
    }

}
