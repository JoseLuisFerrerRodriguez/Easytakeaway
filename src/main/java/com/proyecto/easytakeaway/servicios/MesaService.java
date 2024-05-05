package com.proyecto.easytakeaway.servicios;


import com.proyecto.easytakeaway.dto.MesaDTO;
import com.proyecto.easytakeaway.dto.Paginacion;
import com.proyecto.easytakeaway.excepciones.MesaException;

import java.util.List;

public interface MesaService {

    public static final int MESAS_POR_PAGINA = 5;

    List<MesaDTO> obtenerTodas();

    Paginacion<MesaDTO> listarPorPagina(int numeroPagina);

    MesaDTO getMesa(Integer id) throws MesaException;

    void guardar(MesaDTO mesa);

    void borrarMesa(Integer id) throws MesaException;

    MesaDTO findByNumeroMesa(Integer numeroMesa);
}
