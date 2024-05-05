package com.proyecto.easytakeaway.modelos;

import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import jakarta.persistence.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Entity
@Table(name = "usuariosinfo")
public class UsuarioInfo {
    @Id
    @Column(name = "usuarioID", nullable = false)
    private Integer usuarioID;

    @Column(name = "nombre")
    private String nombre;

    @Column(name = "apellido")
    private String apellido;

    @Column(name = "via")
    private String via;

    @Column(name = "direccion")
    private String direccion;

    @Column(name = "numero")
    private String numero;

    @Column(name = "restodireccion")
    private String restoDireccion;

    @Column(name = "ciudad")
    private String ciudad;

    @Column(name = "codigopostal")
    private String codigoPostal;

    @Column(name = "telefono")
    private String telefono;

    @Column(name = "email")
    private String email;

    @MapsId
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "usuarioID")
    @ToString.Exclude
    private Usuario usuario;

}