/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.proyecto.easytakeaway.modelos;

import com.proyecto.easytakeaway.dto.LineaPedidoDTO;
import com.proyecto.easytakeaway.dto.PedidoDTO;
import com.proyecto.easytakeaway.dto.UsuarioDTO;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Jose
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "pedidos")
public class Pedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "pedidoID")
    private Integer id;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado")
    private EstadoPedido estado;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "usuarioID")
    @ToString.Exclude
    private Usuario usuario;

    @NotNull
    @Column(name = "tipoenvio")
    private Integer tipoEnvio;

    @NotNull
    @Column(name = "tipopago")
    private Integer tipoPago;

    @NotNull
    @Column(name = "pagado")
    private Boolean pagado;

    @Column(name = "Fecha")
    private Timestamp fecha;

    @Column(name = "importetotal")
    private Float importeTotal;

    @OneToOne(mappedBy = "pedido", fetch = FetchType.LAZY, cascade = CascadeType.ALL, optional = true)
    @PrimaryKeyJoinColumn
    @ToString.Exclude
    private Envio envio;

    @ManyToOne(fetch = FetchType.EAGER, optional = true)
    @JoinColumn(name = "MesaID")
    private Mesa mesaID;

    @OneToMany(mappedBy = "pedido")
    @ToString.Exclude
    private List<LineaPedido> lineasPedido;


    public PedidoDTO convertirModeloaDTO(){

        PedidoDTO pedido = new PedidoDTO();

        pedido.setId(this.getId());
        pedido.setEstado(this.getEstado());
        pedido.setFecha(this.getFecha());
        pedido.setTipoEnvio(this.getTipoEnvio());
        pedido.setTipoPago(this.getTipoPago());
        pedido.setImporteTotal(this.getImporteTotal());
        pedido.setPagado(this.getPagado());

        if(this.getUsuario() != null) {
            if(this.getUsuario().getUsuarioInfo() != null) {
                pedido.setNombre(this.getUsuario().getUsuarioInfo().getNombre());
                pedido.setApellidos(this.getUsuario().getUsuarioInfo().getApellido());
                pedido.setEmail(this.getUsuario().getUsuarioInfo().getEmail());
                pedido.setTelefono(this.getUsuario().getUsuarioInfo().getTelefono());
            }
        }

        if(this.getLineasPedido() != null) {
            List<LineaPedidoDTO> lineas = new ArrayList<>();
            for (LineaPedido lineaPedido : lineasPedido) {
                LineaPedidoDTO linea = new LineaPedidoDTO();
                if(lineaPedido.getProducto()!=null) {
                    linea.setNombreProducto(lineaPedido.getProducto().getNombre());
                }

                linea.setCantidad(lineaPedido.getCantidad());
                linea.setIva(lineaPedido.getIva());
                linea.setTotal(lineaPedido.getTotal());
                lineas.add(linea);
            }

            pedido.setLineasPedido(lineas);

        }

        if(this.getEnvio()!=null) {
            pedido.setDireccion(this.getEnvio().getDireccion());
            pedido.setCiudad(this.getEnvio().getCiudad());
            pedido.setCodigoPostal(this.getEnvio().getCodigoPostal());
            pedido.setEstadoEnvio(this.getEnvio().getEstado());
        }

        return pedido;

    }

}
