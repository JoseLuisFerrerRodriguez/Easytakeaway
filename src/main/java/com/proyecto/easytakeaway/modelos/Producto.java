/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.proyecto.easytakeaway.modelos;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Collection;

import com.proyecto.easytakeaway.dto.ProductoDTO;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

/**
 *
 * @author Jose
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "productos")
public class Producto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @NotNull
    @Column(name = "productoID")
    private Integer id;

    @Basic(optional = false)
    @NotNull
    @NotBlank
    @Size(max = 100)
    @Column(name = "nombre")
    private String nombre;

    @Column(name = "alias")
    private String alias;

    @Size(max = 255)
    @Column(name = "descripcion")
    private String descripcion;

    @Basic(optional = false)
    @NotNull
    @Column(name = "precio")
    private Float precio;

    @Basic(optional = false)
    @NotNull
    @Column(name = "iva")
    private Float iva;

    @Column(name = "imagen", columnDefinition="VARCHAR(255) DEFAULT 'default.png'")
    private String imagenURL;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "CategoriaID", referencedColumnName = "id")
    private Categoria categoria;

    @Transient
    public String getNombreCorto() {
        if (nombre.length() > 40) {
            return nombre.substring(0, 40).concat("...");
        }
        return nombre;
    }

    public ProductoDTO convertirModeloaDTO() {

        ProductoDTO producto = new ProductoDTO();
        producto.setId(this.getId());
        producto.setNombre(this.getNombre());
        producto.setNombreCorto(getNombreCorto());
        producto.setAlias(this.getAlias());
        producto.setDescripcion(this.getDescripcion());
        producto.setImagenURL(this.getImagenURL());
        producto.setPrecio(this.getPrecio());
        producto.setIva(this.getIva());
        producto.setCategoria(this.getCategoria().convertirModeloaDTO());

        return producto;

    }
}
