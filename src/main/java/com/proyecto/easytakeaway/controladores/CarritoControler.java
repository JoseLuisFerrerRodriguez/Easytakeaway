package com.proyecto.easytakeaway.controladores;


import com.proyecto.easytakeaway.dto.UsuarioDTO;
import com.proyecto.easytakeaway.modelos.Usuario;
import com.proyecto.easytakeaway.servicios.CarritoService;
import com.proyecto.easytakeaway.servicios.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.webjars.NotFoundException;

import java.security.Principal;

@Controller
public class CarritoControler {

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private CarritoService carritoService;

    @PostMapping("/carrito/añadir/{pid}/{qty}")
    public @ResponseBody String añadirProductoCarrito(@PathVariable("pid") Integer productoId,
                                                      @PathVariable("qty") Integer cantidad,
                                                      Principal principal) {
        UsuarioDTO usuario = obtenerUsuario(principal);

        if(usuario== null)
            return "Debes iniciar sesión para añadir productos";

        Integer cantidadActualizada = carritoService.añadirProducto(productoId, cantidad, usuario);

        return cantidadActualizada > 1 ?
                "Se ha incrementado en "+cantidad + " unidades del producto seleccionado"
                :
                "Se ha añadido una unidad del producto";
    }

    @PostMapping("/carrito/actualizar/{pid}/{qty}")
    public @ResponseBody String actualizarProductoCarrito(@PathVariable("pid") Integer productoId,
                                 @PathVariable("qty") Integer cantidad,
                                 Principal principal) {

        UsuarioDTO usuario = obtenerUsuario(principal);

        if(usuario == null)
            return "Debes iniciar sesión para modificar productos";

        float subtotal = carritoService.actualizarCantidad(productoId, cantidad, usuario);

        return String.valueOf(subtotal);
    }

    @GetMapping("/carrito/borrar/{pid}")
    public  String borrarProductoCarritoGET(@PathVariable("pid") Integer productoId, Principal principal, Model model) {
        UsuarioDTO usuario = obtenerUsuario(principal);

        if(usuario == null) {
            model.addAttribute("errorMessage", new NotFoundException("No se han encontrado pedidos"));
            return "error";
        }

        carritoService.eliminarProducto(productoId, usuario);

        return "redirect:/carrito";
    }


    @PostMapping("/carrito/borrar/{pid}")
    public @ResponseBody String borrarProductoCarrito(@PathVariable("pid") Integer productoId, Principal principal) {
        UsuarioDTO usuario = obtenerUsuario(principal);

        if(usuario == null)
            return "Debes iniciar sesión para borrar productos";

        carritoService.eliminarProducto(productoId, usuario);

        return "El producto ha sido eliminado de la lista de pedido";
    }

    private UsuarioDTO obtenerUsuario(Principal principal) {

        if(principal != null)
            return usuarioService.findByUsuario(principal.getName());

        return null;
    }

}
