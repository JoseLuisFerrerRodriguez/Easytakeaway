package com.proyecto.easytakeaway.repositorios;


import com.proyecto.easytakeaway.modelos.LineaPedido;
import com.proyecto.easytakeaway.modelos.Pedido;
import com.proyecto.easytakeaway.modelos.Producto;
import com.proyecto.easytakeaway.modelos.Usuario;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface PedidoRepository extends JpaRepository<Pedido, Integer> {

    List<Pedido> findOrdersByUsuario(Usuario usuario);

    @Query(nativeQuery=true, value = "SELECT count(pedidoID) FROM pedidos WHERE mesaID = (?1) ")
    Long countByMesa(Integer idMesa);

    @Query("SELECT p FROM Pedido p WHERE p.pagado = false AND p.estado <> 'Cancelado'")
    public Page<Pedido> buscarTodosPendienteCobro(Pageable pageable);

    @Query("SELECT p FROM Pedido p WHERE p.estado = 'procesado'")
    public Page<Pedido> buscarTodosPendienteProcesado(Pageable pageable);

    @Query("SELECT p FROM Pedido p LEFT JOIN Envio e ON p.id=e.id "
            +" WHERE (p.estado = 'elaborado'  AND  p.tipoEnvio=0) "
            +" OR (p.estado = 'elaborado' AND e.estado='pendiente') "
            +" OR (p.estado = 'enviado' AND e.estado='enviado') ")
    public Page<Pedido> buscarTodosPendienteEnvio(Pageable pageable);
}
