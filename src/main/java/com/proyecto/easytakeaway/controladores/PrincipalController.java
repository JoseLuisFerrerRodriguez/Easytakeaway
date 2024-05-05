package com.proyecto.easytakeaway.controladores;

import com.proyecto.easytakeaway.dto.CategoriaDTO;
import com.proyecto.easytakeaway.dto.LineaPedidoDTO;
import com.proyecto.easytakeaway.dto.UsuarioDTO;
import com.proyecto.easytakeaway.excepciones.ProductoException;
import com.proyecto.easytakeaway.modelos.Pedido;
import com.proyecto.easytakeaway.repositorios.CategoriaRepository;
import com.proyecto.easytakeaway.servicios.CategoriaService;
import com.proyecto.easytakeaway.servicios.ProductoService;
import com.proyecto.easytakeaway.servicios.SeguridadService;
import com.proyecto.easytakeaway.servicios.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.webjars.NotFoundException;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("")
public class PrincipalController {

    @Autowired
    private SeguridadService seguridadService;

    @Autowired
    private CategoriaService categoriaService;

    @Autowired
    private ProductoService productoService;

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private CategoriaRepository categoriaRepository;

    @GetMapping("")
    ModelAndView defaultIndex() {
        ModelAndView modelo = new ModelAndView("index");
        modelo.addObject("listaCategorias", categoriaService.obtenerCategoriasActivas());

        try {
            modelo.addObject("listaProductos", productoService.obtenerProductosAleatorios());
        } catch (ProductoException e) {
            return new ModelAndView("error").addObject("errorMessage", e.getMessage());
        }

        if(seguridadService.comprobarLogin()) {
            modelo.addObject("usuariologeado", seguridadService.getUsuarioLogeado());
        }

        return modelo;
    }

    @GetMapping("/index")
    String getIndex() {
        return "redirect:/";
    }


    @GetMapping("/{pid}")
    String getLocalIndex() {
        //TODO
        return "redirect:/";
    }

    @GetMapping("/categorias")
    public String showCategories(Model model) {
        List<CategoriaDTO> categoriasActivas = categoriaService.obtenerCategoriasActivas();
        model.addAttribute("listaCategorias", categoriasActivas);
        return "productos/categorias_vista";
    }

    @GetMapping("/carrito")
    public String showShoppingCard(Model model, Principal principal) {
        if (principal != null) {

            UsuarioDTO usuario = usuarioService.findByUsuario(principal.getName());

            List<LineaPedidoDTO> todasLineasPedido = usuario.getLineasPedido();
            List<LineaPedidoDTO> sinPedido = new ArrayList<>();

            for (LineaPedidoDTO lineaPedidoDTO : todasLineasPedido) {
                if(lineaPedidoDTO.getPedidoId() == null) {
                    sinPedido.add(lineaPedidoDTO);
                }
            }

            model.addAttribute("lineasPedido", sinPedido);
            model.addAttribute("pedido", new Pedido());
        } else {
            model.addAttribute("errorMessage", new NotFoundException("No se han encontrado el carrito"));
            return "error";
        }
        return "carrito";
    }

}
