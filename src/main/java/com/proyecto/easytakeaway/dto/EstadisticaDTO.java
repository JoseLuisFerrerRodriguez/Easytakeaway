package com.proyecto.easytakeaway.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EstadisticaDTO {

    // USUARIO
    private Long totalUsuarios;
    private Long usuariosSinpedidos;
    private Long usuariosSinConfirmarPedido;
    private Map<String, Integer> usuariosPermisos;

    // CATEGORIAS
    private Long totalCategorias;
    private Long categoriasActivas;
    private Long categoriasInactivas;
    private Map<String, Long> productosPorCategorias;

    // PRODUCTOS
    private Long totalProductos;
    private List<String> productosMasVendidos;
    private List<String> productosMenosVendidos;

    // PEDIDOS
    private Long totalPedidos;
    private Map<String, Long> pedidosEstado;

    // DATOS GENERALES
    private Float ventasTotales;
    private Float ventasRecogida;
    private Float ventasDomicilio;
    private Float ventasLocal;
    private Float totalPendiente;

    private Map<String, Double> ventasAnuales;
    private Map<String, Double> ventasMensuales;

}
