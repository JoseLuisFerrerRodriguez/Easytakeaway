package com.proyecto.easytakeaway.controladores;

import com.proyecto.easytakeaway.dto.*;
import com.proyecto.easytakeaway.excepciones.*;
import com.proyecto.easytakeaway.modelos.Rol;
import com.proyecto.easytakeaway.repositorios.RolRepository;
import com.proyecto.easytakeaway.servicios.*;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdministracionController {

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private UsuarioInfoService usuarioInfoService;

    @Autowired
    private CategoriaService categoriaService;

    @Autowired
    private RolRepository rolRepository;

    @Autowired
    private ProductoService productoService;

    @Autowired
    private PedidosService pedidosService;

    @Autowired
    private MesaService mesaService;

    @GetMapping("")
    ModelAndView index(Principal principal) {
        ModelAndView model = new ModelAndView("administracion/main");
        model.addObject("usuario", principal.getName());
        return model;
    }

    @PreAuthorize("hasRole('administrador')")
    @GetMapping("/dashboard")
    public String dashboard(Model model) {

        EstadisticaDTO estadistica = new EstadisticaDTO();

        usuarioService.getEstadistica(estadistica);
        categoriaService.getEstadistica(estadistica);
        productoService.getEstadistica(estadistica);
        pedidosService.getEstadistica(estadistica);

        model.addAttribute("datos", estadistica);
        return "administracion/dashboard";
    }

    // USUARIOS
    @PreAuthorize("hasRole('administrador')")
    @GetMapping("/usuarios")
    public String listarUsuariosRegistrados(Model model){
        return listarUsuariosRegistradosPorPagina(1, model);
    }

    @GetMapping("/usuarios/page/{pageNum}")
    public String listarUsuariosRegistradosPorPagina(@PathVariable(name = "pageNum") int numPagina, Model model) {
        Paginacion<UsuarioDTO> paginacion = usuarioService.listarPorPagina(numPagina);
        inicializadorParametrosPaginacion(numPagina, model, paginacion.getInicio(), paginacion.getFin(), paginacion.getTotalPaginas(), paginacion.getTotalElementos());
        model.addAttribute("usuarios", paginacion.getElementos());
        return "administracion/usuarios/usuario";
    }

    @PreAuthorize("hasRole('administrador')")
    @GetMapping("/usuario/nuevo")
    public String nuevoUsuario(Model model) {
        model.addAttribute("type","crear");
        model.addAttribute("user", new UsuarioDTO());
        model.addAttribute("roles", rolRepository.findAll());
        return "administracion/usuarios/formulario_usuario";
    }

    @PreAuthorize("hasRole('administrador')")
    @PostMapping("/usuario/guardar")
    public String crearUsuario(@Valid @ModelAttribute("user") UsuarioDTO usuarioDTO, BindingResult result, Model model, RedirectAttributes redirect) {

        UsuarioDTO existe = usuarioService.findByUsuario(usuarioDTO.getEmail());

        // Validar el email
        // Si no viene id es un usuario nuevo
        if(usuarioDTO.getId() == null) {
            if (existe != null) {
                result.rejectValue("email", "registro.validacion.cuentaExiste", "Ya existe una cuenta registrada con ese email");
            }
        } else {
            // Si es una modificacion, revisamos que no sea el mismo usuario por su id y que el email no sea el mismo
            if (existe != null && !(existe.getId().equals(usuarioDTO.getId()))
                    && existe.getEmail().equalsIgnoreCase(usuarioDTO.getEmail())) {
                result.rejectValue("email", "registro.validacion.cuentaExiste", "Ya existe una cuenta registrada con ese email");
            }
        }

        Rol rol = rolRepository.findByNombre(usuarioDTO.getRol());
        if(rol != null) {
            usuarioDTO.setRolID(rol.getRolID());
        } else {
            result.rejectValue("rol", "registro.validacion.noExisteRol", "No existe el rol");
        }

        if (result.hasErrors()) {
            model.addAttribute("type",usuarioDTO.getId()==null?"crear":"modificar");
            model.addAttribute("roles", rolRepository.findAll());
            model.addAttribute("user", usuarioDTO);
            return "administracion/usuarios/formulario_usuario";
        }

        usuarioService.guardarUsuario(usuarioDTO);
        redirect.addFlashAttribute("message", "admin.usuarios.guarda.ok");

        return "redirect:/admin/usuarios";
    }

    @PreAuthorize("hasRole('administrador')")
    @GetMapping("/usuario/editar/{id}")
    public String actualizarUsuario(@PathVariable(name = "id") Integer id, Model model, RedirectAttributes redirect) {
        try {
            UsuarioDTO usuario = usuarioService.getUsuario(id);
            model.addAttribute("type","modificar");
            model.addAttribute("user", usuario);
            model.addAttribute("roles", rolRepository.findAll());
        } catch (UsuarioException e) {
            redirect.addFlashAttribute("message", "admin.usuarios.guarda.nook");
            return "redirect:/admin/usuarios";
        }
        return "administracion/usuarios/formulario_usuario";
    }

    @PreAuthorize("hasRole('administrador')")
    @GetMapping("/usuario/borrar/{id}")
    public String borrarUsuario(@PathVariable(name = "id") Integer id, RedirectAttributes redirect) {
        try {
            usuarioService.borrarUsuario(id);
            redirect.addFlashAttribute("message",
                    "El usuario con ID " + id + " ha sido borrado correctamente");
        } catch (UsuarioException e) {
            redirect.addFlashAttribute("messageError", e.getMessage());
        }
        return "redirect:/admin/usuarios";
    }

    // CATEGORIAS
    @GetMapping("/categorias")
    public String listarCategorias(Model model) { return listarCategoriasPorPagina(1, model);  }

    @GetMapping("/categorias/page/{pageNum}")
    public String listarCategoriasPorPagina(@PathVariable(name = "pageNum") int numPagina, Model model) {
        Paginacion<CategoriaDTO> paginacion = categoriaService.listarPorPagina(numPagina);

        inicializadorParametrosPaginacion(numPagina, model, paginacion.getInicio(), paginacion.getFin(), paginacion.getTotalPaginas(), paginacion.getTotalElementos());
        model.addAttribute("categorias", paginacion.getElementos());

        return "administracion/categorias/categoria";
    }

    @GetMapping("/categoria/nuevo")
    public String nuevaCategoria(Model model) {
        List<CategoriaDTO> listaCategorias = categoriaService.obtenerTodas();
        model.addAttribute("nuevo",true);
        model.addAttribute("categoria", new CategoriaDTO());
        model.addAttribute("listaCategorias", listaCategorias);
        return "administracion/categorias/formulario_categoria";
    }

    @PostMapping("/categoria/guardar")
    public String guardarCategoria(@Valid @ModelAttribute("categoria") CategoriaDTO categoriaDTO, BindingResult result, Model model, RedirectAttributes attributes,
                                   @RequestParam("imagenCategoria") MultipartFile imagen) {
        categoriaDTO.setEsNueva(categoriaDTO.getId() == null?true:false);

        if(categoriaDTO.getPadre() != null && categoriaDTO.getPadre().getId() == 0) {
            categoriaDTO.setPadre(null);
        }

        if(categoriaDTO.getEsNueva()) {
            if(categoriaService.existeCategoriaPorNombre(categoriaDTO.getNombre()))
                result.rejectValue("nombre", "admin.categoria.validacion.exiteNombre", "Ya existe una categoria con el nombre indicado");

            if(categoriaService.existeCategoriaPorAlias( categoriaDTO.getAlias()))
                result.rejectValue("alias", "admin.categoria.validacion.exiteAlias", "Ya existe una categoria con el alias indicado");

        } else {
            try {
                CategoriaDTO catAnt = categoriaService.getCategoria(categoriaDTO.getId());
                if(!categoriaDTO.getNombre().equalsIgnoreCase(catAnt.getNombre())) {
                    if(categoriaService.existeCategoriaPorNombre(categoriaDTO.getNombre())) {
                        result.rejectValue("nombre", "admin.categoria.validacion.exiteNombre", "Ya existe una categoria con el nombre indicado");
                    }
                }
                if(!categoriaDTO.getAlias().equalsIgnoreCase(catAnt.getAlias())) {
                    if(categoriaService.existeCategoriaPorAlias( categoriaDTO.getAlias())) {
                        result.rejectValue("alias", "admin.categoria.validacion.exiteAlias", "Ya existe una categoria con el alias indicado");
                    }
                }
            } catch (CategoriaException e) {
                result.rejectValue("nombre", "admin.categoria.actualizar.validarerror", "Ha ocurrido algun error al validar los datos de la actualización ");
            }
        }

        if(result.hasErrors()) {
            List<CategoriaDTO> listaCategorias = categoriaService.obtenerTodas();
            model.addAttribute("nuevo", categoriaDTO.getEsNueva());
            model.addAttribute("mesa", categoriaDTO);
            model.addAttribute("listaCategorias", listaCategorias);
            return "administracion/categorias/formulario_categoria";
        }

        try {
            if(categoriaDTO.getEsNueva())
                categoriaService.guardarCategoria(categoriaDTO, imagen);
            else
                categoriaService.actualizarCategoria(categoriaDTO, imagen);
        } catch (CategoriaException e) {
            attributes.addFlashAttribute("messageError", e.getMessage());
        }

        attributes.addFlashAttribute("message", "admin.categoria.guardar.ok");
        return "redirect:/admin/categorias";
    }

    @GetMapping("/categoria/editar/{id}")
    public String actualizarCategoria(@PathVariable int id, Model model, RedirectAttributes attributes) {
        try {
            CategoriaDTO categoria = categoriaService.getCategoria(id);
            List<CategoriaDTO> listaCategorias = categoriaService.obtenerTodas();
            model.addAttribute("type",false);
            model.addAttribute("categoria", categoria);
            model.addAttribute("listaCategorias", listaCategorias);
            return "administracion/categorias/formulario_categoria";
        } catch (CategoriaException e) {
            attributes.addFlashAttribute("message", e.getMessage());
            return "redirect:/admin/categorias";
        }
    }

    @GetMapping("/categoria/borrar/{id}")
    public String deleteCategory(@PathVariable(name = "id") Integer id, RedirectAttributes redirect) {
        try {
            int productos = productoService.contarProductosPorCategoria(id);

            if(productos>0) {
                redirect.addFlashAttribute("messageError", "admin.categoria.borrar.nook.producto");
            }

            categoriaService.borrarCategoria(id);
            redirect.addFlashAttribute("message", "admin.categoria.borrar.ok");
        } catch (CategoriaException e) {
            redirect.addFlashAttribute("messageError", e.getMessage());
        }
        return "redirect:/admin/categorias";
    }

    // PRODUCTOS
    @GetMapping("/productos")
    public String listarProductos(Model model) {
        return listarProductosPorPagina(1, model, "nombre", "asc", null, 0);
    }

    @GetMapping("/productos/page/{pageNum}")
    public String listarProductosPorPagina(@PathVariable(name = "pageNum") int numPagina, Model model,
                                           @Param("sortField") String sortField,
                                           @Param("sortDir") String sortDir,
                                           @Param("keyword") String keyword,
                                           @Param("categoryId") Integer categoryId) {

        List<CategoriaDTO> listCategories = categoriaService.obtenerTodas();
        Paginacion<ProductoDTO> paginacion = productoService.listarPorPaginaAdmin(categoryId, numPagina, sortField, sortDir, keyword);

        String ordenInverso = sortDir.equals("asc") ? "desc" : "asc";

        inicializadorParametrosPaginacion(numPagina, model, paginacion.getInicio(), paginacion.getFin(), paginacion.getTotalPaginas(), paginacion.getTotalElementos());

        if (categoryId != null) model.addAttribute("categoriaId", categoryId);
        model.addAttribute("productos", paginacion.getElementos());
        model.addAttribute("sortField", sortField);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("reverseSortDir", ordenInverso);
        model.addAttribute("keyword", keyword);
        model.addAttribute("listCategories", listCategories);

        return "administracion/productos/productos";
    }

    @GetMapping("/producto/nuevo")
    public String añadirProducto(Model model) {
        List<CategoriaDTO> categoryList = categoriaService.obtenerTodas();
        model.addAttribute("producto", new ProductoDTO());
        model.addAttribute("categorias", categoryList);
        model.addAttribute("nuevo",true);

        return "administracion/productos/formulario_producto";
    }

    @PostMapping("/producto/guardar")
    public String guardarProducto(@Valid @ModelAttribute("producto") ProductoDTO productoDTO, BindingResult result, Model model, RedirectAttributes redirect,
                                  @RequestParam("imagenProducto") MultipartFile imagen) {

        productoDTO.setEsNueva(productoDTO.getId() == null?true:false);

        if(productoDTO.getEsNueva()) {
            if(productoService.existeProductoPorNombre(productoDTO.getNombre()))
                result.rejectValue("nombre", "admin.productos.validacion.exiteNombre", "Ya existe un producto con el nombre indicado");

            if(productoService.existeProductoPorAlias(productoDTO.getAlias()))
                result.rejectValue("alias", "admin.productos.validacion.exiteAlias", "Ya existe un producto con el alias indicado");

        } else {
            try {
                ProductoDTO prodAnt = productoService.getProducto(productoDTO.getId());
                if(!productoDTO.getNombre().equalsIgnoreCase(prodAnt.getNombre())) {
                    if(productoService.existeProductoPorNombre(productoDTO.getNombre())) {
                        result.rejectValue("nombre", "admin.productos.validacion.exiteNombre", "Ya existe un producto con el nombre indicado");
                    }
                }
                if(!productoDTO.getAlias().equalsIgnoreCase(prodAnt.getAlias())) {
                    if(productoService.existeProductoPorAlias(productoDTO.getAlias())) {
                        result.rejectValue("alias", "admin.productos.validacion.exiteAlias", "Ya existe un producto con el alias indicado");
                    }
                }
            } catch (ProductoException e) {
                result.rejectValue("nombre", "admin.producto.actualizar.validarerror", "Ha ocurrido algun error al validar los datos de la actualización ");
            }
        }

        if(result.hasErrors()) {
            List<CategoriaDTO> categorias = categoriaService.obtenerTodas();
            model.addAttribute("producto", productoDTO);
            model.addAttribute("categorias", categorias);
            model.addAttribute("nuevo", productoDTO.getEsNueva());
            return "administracion/productos/formulario_producto";
        }

        if(productoDTO.getIva()>=1) {
            productoDTO.setIva(productoDTO.getIva()/100);
        }

        try {
            if(productoDTO.getEsNueva())
                productoService.guardarProducto(productoDTO, imagen);
            else
                productoService.actualizarProducto(productoDTO, imagen);
        } catch (ProductoException e) {
            redirect.addFlashAttribute("messageError", e.getMessage());
        }

        redirect.addFlashAttribute("message", "admin.productos.crear.ok");
        return "redirect:/admin/productos";
    }

    @GetMapping("/producto/editar/{id}")
    public String actualizarProducto(@PathVariable(name = "id") int id, Model model, RedirectAttributes attributes) {
        try {
            ProductoDTO producto = productoService.getProducto(id);
            List<CategoriaDTO> categorias = categoriaService.obtenerTodas();

            model.addAttribute("nuevo",false);
            model.addAttribute("producto", producto);
            model.addAttribute("categorias", categorias);
        } catch (ProductoException e) {
            attributes.addFlashAttribute("message", e.getMessage());
            return "redirect:/admin/productos";
        }
        return "administracion/productos/formulario_producto";
    }

    @GetMapping("/producto/borrar/{id}")
    public String deleteProduct(@PathVariable(name = "id") Integer id, RedirectAttributes redirect) {
        try {
            int productos = productoService.contarProductosEnLineas(id);

            if(productos>0) {
                redirect.addFlashAttribute("messageError", "admin.productos.borrar.uso");
            }

            productoService.borrarProducto(id);
            redirect.addFlashAttribute("message", "admin.productos.borrar.ok");
        } catch (ProductoException e) {
            redirect.addFlashAttribute("message", e.getMessage());
        }
        return "redirect:/admin/productos";
    }


    // PEDIDOS
    @GetMapping("/pedidos")
    public String listarPedidos(Model model) {
        return listarPedidosPorPagina(1, model, "fecha", "asc");
    }

    @GetMapping("/pedidos/page/{pageNum}")
    public String listarPedidosPorPagina(@PathVariable(name = "pageNum") int numPagina, Model model,
                                         @Param("sortField") String sortField,
                                         @Param("sortDir") String sortDir) {
        Paginacion<PedidoDTO> paginacion = pedidosService.listarPorPaginaAdmin(numPagina, sortField, sortDir, null);

        String ordenInverso = sortDir.equals("asc") ? "desc" : "asc";

        inicializadorParametrosPaginacion(numPagina, model, paginacion.getInicio(), paginacion.getFin(), paginacion.getTotalPaginas(), paginacion.getTotalElementos());
        model.addAttribute("pedidos", paginacion.getElementos());
        model.addAttribute("sortField", sortField);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("reverseSortDir", ordenInverso);

        return "administracion/pedidos/pedidos";
    }

    @GetMapping("/pedidos/procesar/page/{pageNum}")
    public String listarPedidosPorProcesar(@PathVariable(name = "pageNum") int numPagina, Model model,
                                           @Param("sortField") String sortField,
                                           @Param("sortDir") String sortDir) {

        Paginacion<PedidoDTO> paginacion = pedidosService.listarPorPaginaAdmin(numPagina, sortField, sortDir, "procesar");

        String ordenInverso = sortDir.equals("asc") ? "desc" : "asc";

        inicializadorParametrosPaginacion(numPagina, model, paginacion.getInicio(), paginacion.getFin(), paginacion.getTotalPaginas(), paginacion.getTotalElementos());
        model.addAttribute("pedidos", paginacion.getElementos());
        model.addAttribute("sortField", sortField);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("reverseSortDir", ordenInverso);

        return "administracion/pedidos/pedidosProcesar";
    }

    @GetMapping("/pedidos/enviar/page/{pageNum}")
    public String listarPedidosPorEnviar(@PathVariable(name = "pageNum") int numPagina, Model model,
                                         @Param("sortField") String sortField,
                                         @Param("sortDir") String sortDir) {

        Paginacion<PedidoDTO> paginacion = pedidosService.listarPorPaginaAdmin(numPagina, sortField, sortDir, "enviar");

        String ordenInverso = sortDir.equals("asc") ? "desc" : "asc";

        inicializadorParametrosPaginacion(numPagina, model, paginacion.getInicio(), paginacion.getFin(), paginacion.getTotalPaginas(), paginacion.getTotalElementos());
        model.addAttribute("pedidos", paginacion.getElementos());
        model.addAttribute("sortField", sortField);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("reverseSortDir", ordenInverso);

        return "administracion/pedidos/pedidosEnviar";

    }

    @GetMapping("/pedidos/cobrar/page/{pageNum}")
    public String listarPedidosPorCobrar(@PathVariable(name = "pageNum") int numPagina, Model model,
                                           @Param("sortField") String sortField,
                                           @Param("sortDir") String sortDir) {

        Paginacion<PedidoDTO> paginacion = pedidosService.listarPorPaginaAdmin(numPagina, sortField, sortDir, "cobrar");

        String ordenInverso = sortDir.equals("asc") ? "desc" : "asc";

        inicializadorParametrosPaginacion(numPagina, model, paginacion.getInicio(), paginacion.getFin(), paginacion.getTotalPaginas(), paginacion.getTotalElementos());
        model.addAttribute("pedidos", paginacion.getElementos());
        model.addAttribute("sortField", sortField);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("reverseSortDir", ordenInverso);

        return "administracion/pedidos/pedidosCobrar";
    }

    @GetMapping("/pedidos/cambiarestado/{id}")
    public String pedidoCambiarEstado(@PathVariable(name = "id") int id, Model model,
                                         @Param("estado") String estado) {

        try {
            pedidosService.cambiarEstado(id, estado);
        } catch (PedidoErrorException e) {
            throw new AplicationIException(e.getMessage());
        }

        if(estado.equalsIgnoreCase("cancelado") || estado.equalsIgnoreCase("elaborado")) {
            return listarPedidosPorProcesar(1, model, "fecha", "asc");
        }

        if(estado.equalsIgnoreCase("enviado") || estado.equalsIgnoreCase("entregado")) {
            return listarPedidosPorEnviar(1, model, "fecha", "asc");
        }

        if(estado.equalsIgnoreCase("cobrado")) {
            return listarPedidosPorCobrar(1, model, "fecha", "asc");
        }

        return "administracion/pedidos";
    }


    // MESAS
    @GetMapping("/mesas")
    public String listarMesas(Model model) {
        return listarMesasPorPagina(1, model);
    }

    @GetMapping("/mesas/page/{pageNum}")
    public String listarMesasPorPagina(@PathVariable(name = "pageNum") int numPagina, Model model) {
        Paginacion<MesaDTO> paginacion = mesaService.listarPorPagina(numPagina);
        inicializadorParametrosPaginacion(numPagina, model, paginacion.getInicio(), paginacion.getFin(), paginacion.getTotalPaginas(), paginacion.getTotalElementos());
        model.addAttribute("mesas", paginacion.getElementos());

        return "administracion/mesas/mesas";
    }

    @GetMapping("/mesas/nuevo")
    ModelAndView nuevo() {
        ModelAndView model = new ModelAndView("administracion/mesas/formulario_mesa");
        MesaDTO mesaDTO = new MesaDTO();
        mesaDTO.setEsNueva(true);

        model.addObject("nuevo",true);
        model.addObject("mesa", mesaDTO);
        return model;
    }

    @PostMapping("/mesa/guardar")
    ModelAndView crear(@Valid @ModelAttribute("mesa") MesaDTO mesaDTO, BindingResult result, RedirectAttributes ra) {

        mesaDTO.setEsNueva(mesaDTO.getId() == null?true:false);

        // Validar si existe el numero de mesa sea porque se añade nueva o se modifica a una existente
        MesaDTO existe = mesaService.findByNumeroMesa(mesaDTO.getNumeroMesa());
        if (existe != null) {
            if(mesaDTO.getEsNueva() || (mesaDTO.getNumeroMesa() == existe.getNumeroMesa())) {
                result.rejectValue("numeroMesa", "admin.mesas.validacion.exite", "Ya existe una mesa con el número indicado");
            }
        }

        if(result.hasErrors()) {
            ModelAndView model = new ModelAndView("administracion/mesas/formulario_mesa");
            model.addObject("nuevo", mesaDTO.getEsNueva());
            model.addObject("mesa", mesaDTO);
            return model;
        }

        mesaService.guardar(mesaDTO);
        ra.addFlashAttribute("message","admin.mesas.crear.ok");
        return new ModelAndView("redirect:/admin/mesas");
    }

    @GetMapping("/mesa/editar/{id}")
    public String actualizarMesa(@PathVariable(name = "id") Integer id, Model model, RedirectAttributes redirect) {
        try {
            MesaDTO mesa = mesaService.getMesa(id);
            model.addAttribute("nuevo",false);
            model.addAttribute("mesa", mesa);
        } catch (MesaException e) {
            redirect.addFlashAttribute("messageError", e.getMessage());
            return "redirect:/admin/mesas";
        }
        return "administracion/mesas/formulario_mesa";
    }

    @GetMapping("/mesa/borrar/{id}")
    public String borrarMesa(@PathVariable(name = "id") Integer id, RedirectAttributes redirect){
        try {
            mesaService.borrarMesa(id);
            redirect.addFlashAttribute("message", "EL mesa con ID " + id + " ha sido borrada correctamente");
        } catch (MesaException e) {
            redirect.addFlashAttribute("message", e.getMessage());
        }
        return "redirect:/admin/mesas";
    }

    private void inicializadorParametrosPaginacion(int numeroPagina, Model model, long inicio, long fin,
                                                   int totalPaginas, long totalElementos) {
        if (fin > totalElementos) {
            fin = totalElementos;
        }
        model.addAttribute("currentPage", numeroPagina);
        model.addAttribute("totalPages", totalPaginas);
        model.addAttribute("startCount", inicio);
        model.addAttribute("endCount", fin);
        model.addAttribute("totalItems", totalElementos);
    }


}
