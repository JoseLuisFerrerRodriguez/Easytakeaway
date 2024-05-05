package com.proyecto.easytakeaway.servicios;

import com.proyecto.easytakeaway.dto.CategoriaDTO;
import com.proyecto.easytakeaway.dto.Paginacion;
import com.proyecto.easytakeaway.excepciones.CategoriaException;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface CategoriaService {

    public static final int CATEGORIA_PRINCIPAL_POR_PAGINA = 1;

    List<CategoriaDTO> obtenerCategoriasActivas();

    List<CategoriaDTO> obtenerTodas();

    CategoriaDTO guardarCategoria(CategoriaDTO Categoria, MultipartFile imagen) throws CategoriaException;

    CategoriaDTO actualizarCategoria(CategoriaDTO categoriaDTO, MultipartFile imagen) throws CategoriaException;

    void borrarCategoria(int id) throws CategoriaException;

    CategoriaDTO getCategoria(Integer id) throws CategoriaException;

    CategoriaDTO getCategoriaPorAlias(String alias) throws CategoriaException;

    List<CategoriaDTO> getCategoriaRaiz(CategoriaDTO child);

    boolean existeCategoriaPorNombre(String nombre);

    boolean existeCategoriaPorAlias( String alias);

    Paginacion<CategoriaDTO> listarPorPagina(int numeroPagina);
}
