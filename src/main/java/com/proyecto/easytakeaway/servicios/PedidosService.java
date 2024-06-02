package com.proyecto.easytakeaway.servicios;


import com.proyecto.easytakeaway.dto.*;
import com.proyecto.easytakeaway.excepciones.PedidoErrorException;
import com.proyecto.easytakeaway.modelos.Pedido;

import java.util.List;

public interface PedidosService {

    public static final int PEDIDOS_POR_PAGINA = 10;

    Paginacion<PedidoDTO> listarPorPagina(int numeroPagina);


    Paginacion<PedidoDTO> listarPorPaginaAdmin(int numeroPagina, String campo, String orden, String filtro);

    boolean tienePedidosMesa(Integer idMesa);

    boolean tieneLineasPedidoUsuario(Integer idUsuario);

    List<PedidoDTO> obtenerTodosPorUsuario(UsuarioDTO usuario);

    void guardarNuevoPedido(PedidoDTO nuevoPedido, UsuarioDTO usuario, List<LineaPedidoDTO> lineasPedido) throws PedidoErrorException;

    void cambiarEstado(int id, String estado) throws PedidoErrorException;

    void getEstadistica(EstadisticaDTO estadistica);
}
