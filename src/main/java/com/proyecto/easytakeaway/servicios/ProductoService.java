package com.proyecto.easytakeaway.servicios;

import com.proyecto.easytakeaway.dto.CategoriaDTO;
import com.proyecto.easytakeaway.dto.EstadisticaDTO;
import com.proyecto.easytakeaway.dto.Paginacion;
import com.proyecto.easytakeaway.dto.ProductoDTO;
import com.proyecto.easytakeaway.excepciones.ProductoException;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Set;

public interface ProductoService {

    public static final int PRODUCTOS_POR_PAGINA = 10;
    public static final int PRODUCTOS_POR_BUSQUEDA = 10;
    public static final int PRODUCTOS_POR_PAGINA_ADMIN = 5;

    List<ProductoDTO> obtenerProductosAleatorios() throws ProductoException;

    ProductoDTO getProducto(Integer id) throws ProductoException;

    ProductoDTO getProducto(String alias) throws ProductoException;

    void guardarProducto(ProductoDTO producto);

    ProductoDTO guardarProducto(ProductoDTO productoDTO, MultipartFile imagen) throws ProductoException;

    ProductoDTO actualizarProducto(ProductoDTO productoDTO, MultipartFile imagen) throws ProductoException;

    void borrarProducto(Integer id) throws ProductoException;;

    Paginacion<ProductoDTO> listarPorPagina(int categoriaID, int numeroPagina);

    Paginacion<ProductoDTO> listarPorPaginaAdmin(Integer categoriaID, int numeroPagina, String campo, String orden, String palabra);

    Paginacion<ProductoDTO> busquedaPorPagina(String keyword, int numeroPagina);
    
    int contarProductosPorCategoria(int id);

    Paginacion<ProductoDTO> listarSubCategoriasPorPagina(Set<CategoriaDTO> hijos, int numPagina);

    void getEstadistica(EstadisticaDTO estadistica);

    int contarProductosEnLineas(Integer id);

    boolean existeProductoPorNombre(String nombre);

    boolean existeProductoPorAlias(String alias);


}
