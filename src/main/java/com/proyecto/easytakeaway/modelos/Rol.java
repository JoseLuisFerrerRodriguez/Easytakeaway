/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.proyecto.easytakeaway.modelos;

import java.util.Collection;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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
@Table(name = "roles")
public class Rol {

    @Id
    @Basic(optional = false)
    @NotNull
    @Column(name = "RolID")
    private Integer rolID;

    @Basic(optional = false)
    @NotNull
    @NotBlank
    @Size(max = 50)
    @Column(name = "Nombre")
    private String nombre;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "rol")
    private Collection<Usuario> usuariosCollection;


}
