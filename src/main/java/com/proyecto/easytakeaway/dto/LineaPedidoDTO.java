package com.proyecto.easytakeaway.dto;

import com.proyecto.easytakeaway.modelos.*;
import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class LineaPedidoDTO {

    private Integer id;

    private Integer pedidoId;

    private Integer cantidad;

    private Float precioUnitario;

    private Float iva;

    private Float total;

    private Float subtotal;

    private ProductoDTO producto;

    private String nombreProducto;

    private Integer usuarioID;


    public LineaPedido convertirDTOaModelo() {
        LineaPedido lineaPedido = new LineaPedido();

        lineaPedido.setId(this.getId());

        Pedido pedido = new Pedido();
        pedido.setId(this.getPedidoId());

        lineaPedido.setPedido(pedido);
        lineaPedido.setCantidad(this.getCantidad());
        lineaPedido.setPrecioUnitario(this.getPrecioUnitario());
        lineaPedido.setIva(this.getIva());
        lineaPedido.setTotal(this.getTotal());

        Producto producto = new Producto();
        if(this.getProducto()!=null) {
            producto.setId(this.getProducto().getId());
            producto.setNombre(this.getProducto().getNombre());
        }
        lineaPedido.setProducto(producto);

        Usuario usuario = new Usuario();
        usuario.setUsuarioID(this.getUsuarioID());
        lineaPedido.setUsuario(usuario);

        return lineaPedido;
    }


}