package com.proyecto.easytakeaway.repositorios;


import com.proyecto.easytakeaway.modelos.LineaPedido;
import com.proyecto.easytakeaway.modelos.Producto;
import com.proyecto.easytakeaway.modelos.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;


import java.util.List;

public interface CarritoRepository extends JpaRepository<LineaPedido, Integer> {

    @Query("SELECT lp FROM LineaPedido lp "
            + "WHERE lp.producto.id = ?1 AND lp.usuario.id = ?2 AND lp.pedido is null")
    public LineaPedido buscarLineasPorUsuarioYProducto(Integer productoId, Integer usuarioId);


    @Query(nativeQuery=true, value = "SELECT count(lineaPedidoId) FROM lineaspedido WHERE usuarioID = (?1) ")
    public Long countByUsuario(Integer id);

    @Modifying
    @Query("UPDATE LineaPedido lp SET lp.cantidad = ?1 "
            + "WHERE lp.producto.id = ?2 AND lp.usuario.id = ?3 AND lp.pedido is null")
    public void actualizarCantidad(Integer cantidad, Integer productoId, Integer usuarioId);


    @Modifying
    @Query("DELETE FROM LineaPedido lp "
            +"WHERE lp.usuario.id = ?1 AND lp.producto.id = ?2 AND lp.pedido is null")
    public void borrarPorUsuarioyProducto(Integer id, Integer productoId);
}
