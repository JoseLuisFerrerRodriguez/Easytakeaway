package com.proyecto.easytakeaway.servicios.impl;


import com.proyecto.easytakeaway.dto.CategoriaDTO;
import com.proyecto.easytakeaway.dto.Paginacion;
import com.proyecto.easytakeaway.dto.ProductoDTO;
import com.proyecto.easytakeaway.excepciones.ProductoException;
import com.proyecto.easytakeaway.modelos.Producto;
import com.proyecto.easytakeaway.repositorios.ProductoRepository;
import com.proyecto.easytakeaway.servicios.ProductoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ProductoServiceImpl implements ProductoService {

    @Autowired
    private ProductoRepository repositorio;

    @Override
    public List<ProductoDTO> obtenerProductosAleatorios() throws ProductoException {
        List<Producto> productList = repositorio.findRandom();
        if (productList.isEmpty()) {
            throw new ProductoException("No se ha podido encontrar ningun producto");
        }
        Collections.shuffle(productList);
        int randomSeriesLength = 8;

        return productList.subList(0, randomSeriesLength).stream().map((producto) -> producto.convertirModeloaDTO()).collect(Collectors.toList());

    }

    @Override
    public ProductoDTO getProducto(Integer id) throws ProductoException {
        try {
            return repositorio.getReferenceById(id).convertirModeloaDTO();
        } catch (NoSuchElementException e) {
            throw new ProductoException("No se puede encontrar ningun producto con el id: " + id);
        }

    }

    @Override
    public ProductoDTO getProducto(String alias) throws ProductoException {
        try {
            return repositorio.findByAlias(alias).convertirModeloaDTO();
        } catch (NoSuchElementException e) {
            throw new ProductoException("No se puede encontrar ningun producto con el alias: " + alias);
        }
    }

    @Override
    public void guardarProducto(ProductoDTO producto) {
        if (producto.getAlias() == null || producto.getAlias().isEmpty()) {
            String defaultAlias = producto.getNombre().toLowerCase();
        } else {
            producto.setAlias(producto.getAlias().replaceAll(" ", "_").toLowerCase());
        }
        repositorio.save(producto.convertirDTOaModelo());
    }

    @Override
    public void borrarProducto(Integer id) throws ProductoException {
        Long countById = repositorio.countById(id);
        if (countById == null || countById == 0) {
            throw new ProductoException("No se puede encontrar ningun producto con el id: " + id);
        }
        repositorio.deleteById(id);
    }

    @Override
    public Paginacion<ProductoDTO> listarPorPagina(int categoriaId, int numeroPagina) {
        log.info("listarPorPagina: " + categoriaId + "," +numeroPagina);
        Pageable pageable = PageRequest.of(numeroPagina - 1, PRODUCTOS_POR_PAGINA);

        Page<Producto> pageProductos = repositorio.obtenerPorCategoria(categoriaId, pageable, ("%" + String.valueOf(categoriaId) + "%"));
        return obtenerElementosPorPagina(numeroPagina, pageProductos);
    }

    @Override
    public Paginacion<ProductoDTO> listarPorPaginaAdmin(Integer categoriaId, int numeroPagina, String campo, String orden, String palabra) {
        log.info("listarPorPaginaAdmin: " + categoriaId + "," +numeroPagina);

        Sort sort = Sort.by(campo);
        sort = orden.equals("asc") ? sort.ascending() : sort.descending();

        Pageable pageable = PageRequest.of(numeroPagina - 1, PRODUCTOS_POR_PAGINA_ADMIN, sort);

        if (palabra != null && !palabra.isEmpty()) {
            if (categoriaId != null && categoriaId > 0) {
                String categoryIdMatch = "-" + String.valueOf(categoriaId) + "-";
                Page<Producto> page = repositorio.buscarEnCategoria(categoriaId, categoryIdMatch, palabra, pageable);
                return obtenerElementosPorPagina(numeroPagina, page);
            }
            Page<Producto> page = repositorio.buscarTodos(palabra, pageable);
            return obtenerElementosPorPagina(numeroPagina, page);
        }
        if (categoriaId != null && categoriaId > 0) {
            String categoryIdMatch = "-" + String.valueOf(categoriaId) + "-";
            Page<Producto> page = repositorio.buscarTodosEnCategoria(categoriaId, categoryIdMatch, pageable);
            return obtenerElementosPorPagina(numeroPagina, page);
        }

        Page<Producto> page = repositorio.findAll(pageable);

        return obtenerElementosPorPagina(numeroPagina, page);

    }

    @Override
    public Paginacion<ProductoDTO> busquedaPorPagina(String keyword, int numeroPagina) {
        log.info("listarPorPagina: " + numeroPagina);
        Pageable pageable = PageRequest.of(numeroPagina - 1, PRODUCTOS_POR_BUSQUEDA);

        Page<Producto> pageProductos = repositorio.search(keyword, pageable);
        return obtenerElementosPorPagina(numeroPagina, pageProductos);
    }

    @Override
    public int contarProductosPorCategoria(int id) {
        return repositorio.countByCategoria(id);
    }

    @Override
    public Paginacion<ProductoDTO> listarSubCategoriasPorPagina(Set<CategoriaDTO> hijos, int numPagina) {
        log.info("listarSubCategoriasPorPagina: " + numPagina);
        Pageable pageable = PageRequest.of(numPagina - 1, PRODUCTOS_POR_PAGINA);

        List<Integer> listaIds = new ArrayList<>();
        String todosPadresIDs ="";

        for (CategoriaDTO categoria : hijos) {
            listaIds.add(categoria.getId());
            if(!categoria.getActivado()) {
                todosPadresIDs += "%" + categoria.getId() + "%";
            }
        }

        Page<Producto> pageProductos = repositorio.obtenerPorCategorias(listaIds, pageable, todosPadresIDs) ;
        return obtenerElementosPorPagina(numPagina, pageProductos);
    }

    private Paginacion<ProductoDTO> obtenerElementosPorPagina(int numeroPagina, Page<Producto> pageProductos) {

        long inicio = (numeroPagina - 1) * PRODUCTOS_POR_BUSQUEDA + 1;
        long fin = inicio + PRODUCTOS_POR_BUSQUEDA - 1;

        Paginacion<ProductoDTO> paginacion = new Paginacion<ProductoDTO>();
        paginacion.setInicio(inicio);
        paginacion.setFin(fin);
        paginacion.setTotalPaginas(pageProductos.getTotalPages());
        paginacion.setTotalElementos(pageProductos.getTotalElements());

        List<ProductoDTO> lista = new ArrayList<ProductoDTO>();
        List<Producto> listadoModelo = pageProductos.getContent();

        for (int i = 0; i < listadoModelo.size(); i++) {
            Producto producto =  listadoModelo.get(i);
            lista.add(producto.convertirModeloaDTO());
        }

        paginacion.setElementos(lista);

        return paginacion;

    }


}
