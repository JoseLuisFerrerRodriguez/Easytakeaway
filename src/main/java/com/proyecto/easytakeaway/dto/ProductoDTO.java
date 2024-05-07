/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.proyecto.easytakeaway.dto;

import com.proyecto.easytakeaway.modelos.Producto;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
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
    @Size(max = 150)
    private String nombre;

    private String nombreCorto;

    private String alias;

    private String descripcion;

    @NotNull
    @Digits(message="{admin.productos.validacion.tiponumero}", fraction = 2, integer = 2)
    @DecimalMin(value = "0.01", message="{admin.productos.validacion.precio}")
    @DecimalMax(value="10000", message="{admin.productos.validacion.precio}")
    @Column(name = "precio")
    private Float precio;

    @NotNull
    @Digits(message="{admin.productos.validacion.tiponumero}", fraction = 2, integer = 2)
    @DecimalMin(value = "0.01", message="{admin.productos.validacion.iva}")
    @DecimalMax(value="99", message="{admin.productos.validacion.iva}")
    private Float iva;

    private String imagenURL;

    private CategoriaDTO categoria;

    private Boolean esNueva;

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
