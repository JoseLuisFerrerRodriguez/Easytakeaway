package com.proyecto.easytakeaway.dto;

import com.proyecto.easytakeaway.modelos.*;
import lombok.*;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PedidoDTO {

    private Integer id;

    private EstadoPedido estado;

    private Integer tipoEnvio;

    private Integer tipoPago;

    private Timestamp fecha;

    private Float importeTotal;

    private Boolean pagado;

    // Usuario
    private String nombre;

    private String apellidos;

    private String email;

    private String telefono;

    // Envio
    private String direccion;

    private String ciudad;

    private String codigoPostal;

    private EstadoEnvio estadoEnvio;

    // Lineas
    private List<LineaPedidoDTO> lineasPedido;

    // Mesa
    private Mesa mesa;

    public Pedido convertirDTOaModelo() {
        Pedido pedido = new Pedido();

        pedido.setId(this.getId());
        pedido.setEstado(this.estado);
        pedido.setTipoEnvio(this.getTipoEnvio());
        pedido.setTipoPago(this.getTipoPago());
        pedido.setPagado(this.getPagado());
        pedido.setFecha(this.getFecha());
        pedido.setImporteTotal(this.getImporteTotal());

        Envio envio = new Envio();
        envio.setPedidoId(this.getId());
        envio.setDireccion(this.getDireccion());
        envio.setCiudad(this.getCiudad());
        envio.setCodigoPostal(this.getCodigoPostal());

        pedido.setEnvio(envio);

        return pedido;
    }

}
