package com.proyecto.easytakeaway.servicios;

import com.proyecto.easytakeaway.dto.UsuarioDTO;

public interface CarritoService {

    Integer añadirProducto(Integer productoId, Integer cantidad, UsuarioDTO usuario);

    float actualizarCantidad(Integer productoId, Integer cantidad, UsuarioDTO usuario);

    void eliminarProducto(Integer productoId, UsuarioDTO usuario);

}
