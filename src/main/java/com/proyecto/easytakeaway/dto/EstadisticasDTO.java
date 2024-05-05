package com.proyecto.easytakeaway.dto;

import com.proyecto.easytakeaway.modelos.Categoria;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EstadisticasDTO {

    private Integer totalUsuarios;

    private Float gananciasTotales;

    private Integer totalPedidos;

    private Integer totalPedidosCancelados;

    private Integer total;

    private Integer totalPedidosCompletados;

}
