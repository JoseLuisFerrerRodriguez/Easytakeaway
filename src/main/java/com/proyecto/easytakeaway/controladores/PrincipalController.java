package com.proyecto.easytakeaway.controladores;

import com.proyecto.easytakeaway.dto.CategoriaDTO;
import com.proyecto.easytakeaway.dto.LineaPedidoDTO;
import com.proyecto.easytakeaway.dto.MesaDTO;
import com.proyecto.easytakeaway.dto.UsuarioDTO;
import com.proyecto.easytakeaway.excepciones.MesaException;
import com.proyecto.easytakeaway.excepciones.ProductoException;
import com.proyecto.easytakeaway.modelos.Pedido;
import com.proyecto.easytakeaway.repositorios.CategoriaRepository;
import com.proyecto.easytakeaway.servicios.CategoriaService;
import com.proyecto.easytakeaway.servicios.ProductoService;
import com.proyecto.easytakeaway.servicios.SeguridadService;
import com.proyecto.easytakeaway.servicios.UsuarioService;
import com.proyecto.easytakeaway.servicios.MesaService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.webjars.NotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("")
public class PrincipalController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private SeguridadService seguridadService;

    @Autowired
    private CategoriaService categoriaService;

    @Autowired
    private ProductoService productoService;

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private MesaService mesaService;

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


    @GetMapping("/{mId}")
    ModelAndView getLocalIndex(@PathVariable("mId") String mId, HttpServletRequest request) {

        ModelAndView modelo = new ModelAndView("index");

        MesaDTO mesa;

        String numeroMesa = seguridadService.decodificarText(mId);
        mesa = mesaService.findByNumeroMesa(Integer.parseInt(numeroMesa));

        if(mesa == null) {
            return defaultIndex();
        }

        HttpSession session = request.getSession(true);
        SecurityContext securityContext = loginDefaultUser();
        session.setAttribute("SPRING_SECURITY_CONTEXT", securityContext);
        session.setAttribute("mesa", mesa);

        modelo.addObject("listaCategorias", categoriaService.obtenerCategoriasActivas());

        try {
            modelo.addObject("listaProductos", productoService.obtenerProductosAleatorios());
        } catch (ProductoException e) {
            return new ModelAndView("error").addObject("errorMessage", e.getMessage());
        }

        return modelo;
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

    private SecurityContext loginDefaultUser() {

        String username = "default@email.com";
        String password = "default!13579";

        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(username, password);

        Authentication authentication = authenticationManager.authenticate(authenticationToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        return SecurityContextHolder.getContext();

    }

}
