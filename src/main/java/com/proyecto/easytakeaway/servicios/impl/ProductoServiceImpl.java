package com.proyecto.easytakeaway.servicios.impl;


import com.proyecto.easytakeaway.configuracion.AppConstants;
import com.proyecto.easytakeaway.dto.CategoriaDTO;
import com.proyecto.easytakeaway.dto.EstadisticaDTO;
import com.proyecto.easytakeaway.dto.Paginacion;
import com.proyecto.easytakeaway.dto.ProductoDTO;
import com.proyecto.easytakeaway.excepciones.CategoriaException;
import com.proyecto.easytakeaway.excepciones.ProductoException;
import com.proyecto.easytakeaway.modelos.Categoria;
import com.proyecto.easytakeaway.modelos.Producto;
import com.proyecto.easytakeaway.repositorios.ProductoRepository;
import com.proyecto.easytakeaway.servicios.FicheroService;
import com.proyecto.easytakeaway.servicios.ProductoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ProductoServiceImpl implements ProductoService {

    @Autowired
    private ProductoRepository repositorio;

    @Autowired
    private FicheroService ficheroService;

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

    @Transactional
    @Override
    public ProductoDTO guardarProducto(ProductoDTO productoDTO, MultipartFile imagen) throws ProductoException {

        log.info("guardarProducto: "+ productoDTO);

        Producto producto = repositorio.save(productoDTO.convertirDTOaModelo());

        if(imagen != null && !imagen.isEmpty() ) {
            try {
                String path = AppConstants.URL_IMAGENES_PRODUCTOS;

                String extension = ficheroService.obtenerExtension(imagen);
                String nombreFichero = producto.getId() + "." + extension;
                producto.setImagenURL(nombreFichero);

                if(ficheroService.existeImagen(path, producto.getImagenURL())) {
                    if(!producto.getImagenURL().equalsIgnoreCase("default.png"))
                        ficheroService.borrarImagen(path, productoDTO.getImagenURL());
                }
                ficheroService.guardarImagen(path, imagen, producto.getImagenURL());

                // Se guarda de nuevo la imagen porque no teniamos el id para la imagen
                repositorio.save(producto);
            } catch (IOException e) {
                log.error("Error al borrar o guardar la imagen. " + e.getMessage());
                throw new ProductoException(ProductoException.ERROR_GUARDAR);
            }
        }

        return producto.convertirModeloaDTO();

    }

    @Transactional
    @Override
    public ProductoDTO actualizarProducto(ProductoDTO productoDTO, MultipartFile imagen) throws ProductoException {
        log.info("actualizarProducto:" +productoDTO);

        try {
            Producto anterior = repositorio.getReferenceById(productoDTO.getId());

            anterior.setNombre(productoDTO.getNombre());
            anterior.setAlias(productoDTO.getAlias());
            anterior.setDescripcion(productoDTO.getDescripcion());
            anterior.setImagenURL(productoDTO.getImagenURL()!=null?productoDTO.getImagenURL():anterior.getImagenURL());
            anterior.setPrecio(productoDTO.getPrecio());
            anterior.setIva(productoDTO.getIva());

            Producto producto = repositorio.save(anterior);

            // Despues de guardar cambiamos la imagen por si va mal hace rollback
            if(imagen != null && !imagen.isEmpty()) {
                String path = AppConstants.URL_IMAGENES_PRODUCTOS;

                try {
                    if(ficheroService.existeImagen(path, producto.getImagenURL())) {
                        if(!producto.getImagenURL().equalsIgnoreCase("default.png"))
                            ficheroService.borrarImagen(path, producto.getImagenURL());
                    }

                    String extension = ficheroService.obtenerExtension(imagen);
                    String nombreFichero = producto.getId() + "." + extension;
                    producto.setImagenURL(nombreFichero);

                    ficheroService.guardarImagen(path, imagen, nombreFichero);

                } catch (IOException e) {
                    log.error("Error al borrar o guardar la imagen. " + e.getMessage());
                    throw new ProductoException(ProductoException.ERROR_ACTUALIZAR);
                }
            }

            return producto.convertirModeloaDTO();

        } catch (Exception e) {
            log.error("Error al actualizar la categoria. " + e.getMessage());
            throw new ProductoException(ProductoException.ERROR_ACTUALIZAR);
        }


    }

    @Transactional
    @Override
    public void borrarProducto(Integer id) throws ProductoException {

        log.info("borrar producto:" +id);
        Producto producto = repositorio.getReferenceById(id);

        if(producto == null) {
            log.error("Validacion. No existe ningun producto con ese id");
            throw new ProductoException(ProductoException.NO_EXISTE);
        }

        try {
            if(!producto.getImagenURL().equalsIgnoreCase("default.png")) {
                if(ficheroService.existeImagen(AppConstants.URL_IMAGENES_PRODUCTOS, producto.getImagenURL())) {
                    ficheroService.borrarImagen(AppConstants.URL_IMAGENES_PRODUCTOS, producto.getImagenURL());
                }
            }
        } catch (IOException e) {
            log.error("Error al borrar la imagen. " + e.getMessage());
            throw new ProductoException(ProductoException.ERROR_BORRAR);
        }

        repositorio.deleteById(id);
    }

    @Override
    public Paginacion<ProductoDTO> listarPorPagina(int categoriaId, int numeroPagina) {
        log.info("listarPorPagina: " + categoriaId + "," +numeroPagina);
        Pageable pageable = PageRequest.of(numeroPagina - 1, PRODUCTOS_POR_PAGINA);

        Page<Producto> pageProductos = repositorio.obtenerPorCategoria(categoriaId, pageable, ("%" + String.valueOf(categoriaId) + "%"));
        return obtenerElementosPorPagina(numeroPagina, pageProductos, PRODUCTOS_POR_PAGINA);
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
                return obtenerElementosPorPagina(numeroPagina, page, PRODUCTOS_POR_PAGINA_ADMIN);
            }
            Page<Producto> page = repositorio.buscarTodos(palabra, pageable);
            return obtenerElementosPorPagina(numeroPagina, page, PRODUCTOS_POR_PAGINA_ADMIN);
        }
        if (categoriaId != null && categoriaId > 0) {
            String categoryIdMatch = "-" + String.valueOf(categoriaId) + "-";
            Page<Producto> page = repositorio.buscarTodosEnCategoria(categoriaId, categoryIdMatch, pageable);
            return obtenerElementosPorPagina(numeroPagina, page, PRODUCTOS_POR_PAGINA_ADMIN);
        }

        Page<Producto> page = repositorio.findAll(pageable);

        return obtenerElementosPorPagina(numeroPagina, page, PRODUCTOS_POR_PAGINA_ADMIN);

    }

    @Override
    public Paginacion<ProductoDTO> busquedaPorPagina(String keyword, int numeroPagina) {
        log.info("listarPorPagina: " + numeroPagina);
        Pageable pageable = PageRequest.of(numeroPagina - 1, PRODUCTOS_POR_BUSQUEDA);

        Page<Producto> pageProductos = repositorio.search(keyword, pageable);
        return obtenerElementosPorPagina(numeroPagina, pageProductos, PRODUCTOS_POR_BUSQUEDA);
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
        return obtenerElementosPorPagina(numPagina, pageProductos, PRODUCTOS_POR_BUSQUEDA);
    }

    @Override
    public void getEstadistica(EstadisticaDTO estadistica) {

        Long totalproductos = repositorio.count();
        List<String> productosMasVendidos = repositorio.obtenerProductosMasVendidos();

        List<String> listaFiltradaMasVendidos = new ArrayList<>();
        List<String> listaFiltradaMenosVendidos = new ArrayList<>();

        if(productosMasVendidos!=null && !productosMasVendidos.isEmpty()) {

            if(productosMasVendidos.size()<=3) {
                listaFiltradaMasVendidos = productosMasVendidos;
            } else if(productosMasVendidos.size()<=6) {
                listaFiltradaMasVendidos = productosMasVendidos.subList(0,3);
                listaFiltradaMenosVendidos = productosMasVendidos.subList(3,productosMasVendidos.size());
            } else {
                listaFiltradaMasVendidos = productosMasVendidos.subList(0,3);
                listaFiltradaMenosVendidos = productosMasVendidos.subList(productosMasVendidos.size()-3,productosMasVendidos.size());
            }

        }
        estadistica.setTotalProductos(totalproductos==null?0:totalproductos);
        estadistica.setProductosMasVendidos(listaFiltradaMasVendidos);
        estadistica.setProductosMenosVendidos(listaFiltradaMenosVendidos);
    }

    @Override
    public int contarProductosEnLineas(Integer id) {
        return repositorio.countByProductoEnLineasPedido(id);
    }

    @Override
    public boolean existeProductoPorNombre(String nombre) {
        log.info("existeProductoPorNombre: "+nombre);

        Producto producto = repositorio.findByNombre(nombre);

        if (producto != null) {
            return true;
        }

        return false;
    }

    @Override
    public boolean existeProductoPorAlias(String alias) {
        log.info("existeProductoPorAlias: "+alias);

        Producto producto = repositorio.findByAlias(alias);

        if (producto != null) {
            return true;
        }

        return false;
    }

    private Paginacion<ProductoDTO> obtenerElementosPorPagina(int numeroPagina, Page<Producto> pageProductos, int cantidad) {

        long inicio = (numeroPagina - 1) * cantidad + 1;
        long fin = inicio + cantidad - 1;

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
