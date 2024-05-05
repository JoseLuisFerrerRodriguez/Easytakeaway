/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.proyecto.easytakeaway.modelos;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.proyecto.easytakeaway.dto.LineaPedidoDTO;
import com.proyecto.easytakeaway.dto.UsuarioDTO;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 *
 * @author Jose
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "usuarios")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "UsuarioID")
    private Integer usuarioID;

    @NotNull
    @Size(min = 1, max = 50)
    @Column(name = "login")
    private String login;

    @NotNull
    @Size(min = 1, max = 64)
    @Column(name = "Password")
    private String password;

    @JoinColumn(name = "RolID", referencedColumnName = "RolID")
    @ManyToOne(optional = false)
    private Rol rol;

    @OneToOne(fetch = FetchType.LAZY, mappedBy = "usuario", cascade = CascadeType.ALL)
    @PrimaryKeyJoinColumn
    @ToString.Exclude
    private UsuarioInfo usuarioInfo;

    @OneToMany(mappedBy = "usuario", fetch = FetchType.EAGER, cascade = {CascadeType.ALL, CascadeType.PERSIST})
    @ToString.Exclude
    private List<LineaPedido> lineasPedido;

    @OneToMany(mappedBy = "usuario")
    @ToString.Exclude
    private List<Pedido> pedidos;

    public UsuarioDTO convertirModeloaDTO(){
        UsuarioDTO usuarioDTO = new UsuarioDTO();
        usuarioDTO.setId(this.getUsuarioID());
        usuarioDTO.setUsername(this.getLogin());
        usuarioDTO.setPassword(this.getPassword());

        UsuarioInfo usuarioInfo = this.getUsuarioInfo();
        if(usuarioInfo != null) {
            usuarioDTO.setNombre(usuarioInfo.getNombre());
            usuarioDTO.setApellidos(usuarioInfo.getApellido());
            usuarioDTO.setVia(usuarioInfo.getVia());
            usuarioDTO.setDireccion(usuarioInfo.getDireccion());
            usuarioDTO.setNumero(usuarioInfo.getNumero());
            usuarioDTO.setRestoDireccion(usuarioInfo.getRestoDireccion());
            usuarioDTO.setCiudad(usuarioInfo.getCiudad());
            usuarioDTO.setCodigoPostal(usuarioInfo.getCodigoPostal());
            usuarioDTO.setTelefono(usuarioInfo.getTelefono());
            usuarioDTO.setEmail(usuarioInfo.getEmail());
        }

        Rol rol = this.getRol();
        usuarioDTO.setRolID(rol.getRolID());
        usuarioDTO.setRol(rol.getNombre());

        if(this.getLineasPedido() != null) {

            List<LineaPedidoDTO> lineasPedido = new ArrayList<>();

            for (LineaPedido lp : this.getLineasPedido()) {
                lineasPedido.add(lp.convertirModeloaDTO());
            }

            usuarioDTO.setLineasPedido(lineasPedido);
        }

        return usuarioDTO;
    }

}
