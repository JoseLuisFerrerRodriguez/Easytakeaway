package com.proyecto.easytakeaway.controladores;

import com.proyecto.easytakeaway.dto.CategoriaDTO;
import com.proyecto.easytakeaway.dto.Paginacion;
import com.proyecto.easytakeaway.dto.ProductoDTO;
import com.proyecto.easytakeaway.excepciones.CategoriaException;
import com.proyecto.easytakeaway.excepciones.ProductoException;
import com.proyecto.easytakeaway.servicios.CategoriaService;
import com.proyecto.easytakeaway.servicios.ProductoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.ArrayList;
import java.util.List;

@Controller
public class ProductoController {

    @Autowired
    private CategoriaService categoriaService;

    @Autowired
    private ProductoService productoService;

    // CATEGORIAS
    @GetMapping({"/categoria/{alias}"})
    public String vistaCategoriaPrimeraPagina(@PathVariable("alias") String alias, Model model) {
        return vistaCategoriaPorPagina(alias, model, 1);
    }

    @GetMapping("/categoria/{alias}/page/{pageNum}")
    public String vistaCategoriaPorPagina(@PathVariable("alias") String alias, Model model,
                                          @PathVariable("pageNum") int numPagina) {
        try {
            CategoriaDTO categoria = categoriaService.getCategoriaPorAlias(alias);

            List<CategoriaDTO> listaCategoriasRaiz = categoriaService.getCategoriaRaiz(categoria);

            Paginacion<ProductoDTO> paginacion = new Paginacion<ProductoDTO>();

            int totalProductos = productoService.contarProductosPorCategoria(categoria.getId());
            if(totalProductos == 0) {
                // Si hay 0 productos puede ser que sea una categoria raíz y clasificadora
                paginacion = productoService.listarSubCategoriasPorPagina(categoria.getHijos(), numPagina);
            } else {
                paginacion = productoService.listarPorPagina(categoria.getId(), numPagina);
            }

            // Recorremos los hijos por si hay alguno inactivo para saltarlo y mostrar las categorias subyacientes
            List<CategoriaDTO> nuevosSubHijos = new ArrayList<>();
            for (CategoriaDTO hijo : categoria.getHijos()) {
                if(!hijo.getActivado()) {
                    nuevosSubHijos.addAll(hijo.getHijos());
                }
            }
            categoria.getHijos().addAll(nuevosSubHijos);

            inicializadorParametrosPaginacion(numPagina, model, paginacion.getInicio(), paginacion.getFin(), paginacion.getTotalPaginas(), paginacion.getTotalElementos());

            model.addAttribute("listaCategoriasRaiz", listaCategoriasRaiz);
            model.addAttribute("listaProductos", paginacion.getElementos());
            model.addAttribute("categoria", categoria);

            return "productos/productos_categoria_vista";
        } catch (CategoriaException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "error";
        }

    }

    // PRODUCTO
    @GetMapping("/producto/{alias}")
    public String vistaDetalleProducto(@PathVariable("alias") String alias, Model model) {
        try {
            ProductoDTO producto = productoService.getProducto(alias);
            List<CategoriaDTO> listaCategoriasRaiz = categoriaService.getCategoriaRaiz(producto.getCategoria());
            model.addAttribute("listaCategoriasRaiz", listaCategoriasRaiz);
            model.addAttribute("producto", producto);
            return "productos/producto_vista";
        } catch (ProductoException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "error";
        }
    }

    @GetMapping("/buscar")
    public String buscarPrimeraPagina(@Param("keyword") String keyword, Model model) {
        return buscarPorPagina(keyword, 1, model);
    }

    @GetMapping("/buscar/page/{pageNum}")
    public String buscarPorPagina(@Param("keyword") String keyword, @PathVariable("pageNum") int numPagina, Model model) {

        Paginacion<ProductoDTO> paginacion = productoService.busquedaPorPagina(keyword, numPagina);
        inicializadorParametrosPaginacion(numPagina, model, paginacion.getInicio(), paginacion.getFin(), paginacion.getTotalPaginas(), paginacion.getTotalElementos());

        model.addAttribute("pageTitle", StringUtils.capitalize(keyword) + " - Resultado de busqueda");
        model.addAttribute("keyword", keyword);
        model.addAttribute("resultList", paginacion.getElementos());
        return "productos/productos_busqueda";
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
