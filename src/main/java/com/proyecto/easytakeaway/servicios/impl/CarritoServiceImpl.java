package com.proyecto.easytakeaway.servicios.impl;


import com.proyecto.easytakeaway.dto.UsuarioDTO;
import com.proyecto.easytakeaway.modelos.LineaPedido;
import com.proyecto.easytakeaway.modelos.Producto;
import com.proyecto.easytakeaway.modelos.Usuario;
import com.proyecto.easytakeaway.repositorios.CarritoRepository;
import com.proyecto.easytakeaway.repositorios.ProductoRepository;
import com.proyecto.easytakeaway.servicios.CarritoService;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
public class CarritoServiceImpl implements CarritoService {

    @Autowired
    private CarritoRepository carritoRepository;

    @Autowired
    private ProductoRepository productoRepository;

    @Override
    public Integer añadirProducto(Integer productoId, Integer cantidad, UsuarioDTO usuario) {

        Integer cantidadAñadir = cantidad;
        Usuario usuarioModelo = usuario.convertirDTOaModelo();

        Producto producto = productoRepository.getReferenceById(productoId);

        LineaPedido lineaPedido = carritoRepository.buscarLineasPorUsuarioYProducto(producto.getId(), usuarioModelo.getUsuarioID());

        if (lineaPedido != null) {
            cantidadAñadir = lineaPedido.getCantidad() + cantidad;
        }  else {
            lineaPedido = new LineaPedido();
            lineaPedido.setUsuario(usuarioModelo);
            lineaPedido.setProducto(producto);
        }

        lineaPedido.setCantidad(cantidadAñadir);

        carritoRepository.save(lineaPedido);

        return cantidadAñadir;
    }

    @Override
    @Transactional
    public float actualizarCantidad(Integer productoId, Integer cantidad, UsuarioDTO usuario) {
        carritoRepository.actualizarCantidad(cantidad, productoId, usuario.getId());
        Producto producto = productoRepository.getReferenceById(productoId);
        return producto.getPrecio() * cantidad;
    }

    @Override
    @Transactional
    public void eliminarProducto(Integer productoId, UsuarioDTO usuario) {
        carritoRepository.borrarPorUsuarioyProducto(usuario.getId(), productoId);
    }
}
