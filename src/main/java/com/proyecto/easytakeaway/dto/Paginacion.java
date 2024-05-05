package com.proyecto.easytakeaway.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Paginacion <T> {

    private long inicio;

    private long fin;
    
    private int totalPaginas;

    private long totalElementos;

    private List<T> elementos;

}
