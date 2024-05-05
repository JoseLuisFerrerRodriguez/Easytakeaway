package com.proyecto.easytakeaway.modelos;

import com.proyecto.easytakeaway.dto.CategoriaDTO;
import com.proyecto.easytakeaway.dto.LineaPedidoDTO;
import com.proyecto.easytakeaway.dto.ProductoDTO;
import lombok.*;

import jakarta.persistence.*;


@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Entity
@Table(name = "lineaspedido")
public class LineaPedido {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "lineapedidoid")
    private int id;

    @ManyToOne(fetch = FetchType.EAGER, optional = true)
    @JoinColumn(name = "pedidoID")
    @ToString.Exclude
    private Pedido pedido;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "productoID")
    @ToString.Exclude
    private Producto producto;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "usuarioID")
    @ToString.Exclude
    private Usuario usuario;

    @Column(name = "cantidad")
    private int cantidad;

    @Column(name = "preciounitario")
    private float precioUnitario;

    @Column(name = "iva")
    private float iva;

    @Column(name = "total")
    private float total;

    @Transient
    public float getSubtotal() {
        return this.producto.getPrecio() * cantidad;
    }


    public LineaPedidoDTO convertirModeloaDTO(){
        LineaPedidoDTO lineaPedidoDTO = new LineaPedidoDTO();

        lineaPedidoDTO.setId(this.getId());
        if(this.getPedido()!=null) {
            lineaPedidoDTO.setPedidoId(this.getPedido().getId());
        }
        lineaPedidoDTO.setCantidad(this.getCantidad());
        lineaPedidoDTO.setPrecioUnitario(this.getPrecioUnitario());
        lineaPedidoDTO.setIva(this.getIva());
        lineaPedidoDTO.setTotal(this.getTotal());
        lineaPedidoDTO.setSubtotal(this.getSubtotal());

        if(this.getProducto()!=null) {
            ProductoDTO productoDTO = new ProductoDTO();
            productoDTO.setId(this.getProducto().getId());
            productoDTO.setNombre(this.getProducto().getNombre());
            productoDTO.setDescripcion(this.getProducto().getDescripcion());
            productoDTO.setImagenURL(this.getProducto().getImagenURL());
            productoDTO.setPrecio(this.getProducto().getPrecio());
            productoDTO.setIva(this.getProducto().getIva());
            productoDTO.setAlias(this.getProducto().getAlias());

            if(this.getProducto().getCategoria() != null) {
                CategoriaDTO categoriaDTO = new CategoriaDTO();
                categoriaDTO.setId(this.getProducto().getCategoria().getId());
                categoriaDTO.setNombre(this.getProducto().getCategoria().getNombre());
                productoDTO.setCategoria(categoriaDTO);
            }


            lineaPedidoDTO.setProducto(productoDTO);
        }

        if(this.getUsuario()!=null) {
            lineaPedidoDTO.setUsuarioID(this.getUsuario().getUsuarioID());
        }

        return lineaPedidoDTO;
    }


}