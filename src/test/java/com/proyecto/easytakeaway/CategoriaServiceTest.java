package com.proyecto.easytakeaway;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import com.proyecto.easytakeaway.dto.CategoriaDTO;
import com.proyecto.easytakeaway.dto.EstadisticaDTO;
import com.proyecto.easytakeaway.dto.Paginacion;
import com.proyecto.easytakeaway.modelos.Categoria;
import com.proyecto.easytakeaway.repositorios.CategoriaRepository;
import com.proyecto.easytakeaway.servicios.impl.CategoriaServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;


@ExtendWith(MockitoExtension.class)
public class CategoriaServiceTest {

    @Mock
    private CategoriaRepository repositorio;

    @InjectMocks
    private CategoriaServiceImpl categoriaService;

    @Test
    void testObtenerCategoriasActivas() {
        Categoria categoria1 = new Categoria();
        categoria1.setNombre("Categoria 1");
        categoria1.setActivado(true);

        Categoria categoria2 = new Categoria();
        categoria2.setNombre("Categoria 2");
        categoria2.setActivado(true);

        List<Categoria> categoriasActivas = Arrays.asList(categoria1, categoria2);

        when(repositorio.buscarActivas()).thenReturn(categoriasActivas);

        List<CategoriaDTO> result = categoriaService.obtenerCategoriasActivas();

        assertThat(result).isNotEmpty();
        assertThat(result.size()).isEqualTo(2);
    }

    @Test
    void testObtenerTodas() {
        Categoria categoria1 = new Categoria();
        categoria1.setNombre("Categoria 1");

        Categoria categoria2 = new Categoria();
        categoria2.setNombre("Categoria 2");

        List<Categoria> categorias = Arrays.asList(categoria1, categoria2);

        when(repositorio.findAll()).thenReturn(categorias);

        List<CategoriaDTO> result = categoriaService.obtenerTodas();

        assertThat(result).isNotEmpty();
        assertThat(result.size()).isEqualTo(2);
    }

    @Test
    void testGuardarCategoria() throws Exception {
        Categoria categoria = new Categoria();
        categoria.setNombre("Categoria 1");

        CategoriaDTO categoriaDTO = new CategoriaDTO();
        categoriaDTO.setNombre("Categoria 1");

        when(repositorio.save(any(Categoria.class))).thenReturn(categoria);

        CategoriaDTO result = categoriaService.guardarCategoria(categoriaDTO, null);

        assertThat(result).isNotNull();
        assertThat(result.getNombre()).isEqualTo("Categoria 1");
    }

    @Test
    void testActualizarCategoria() throws Exception {
        Categoria categoria = new Categoria();
        categoria.setId(1);
        categoria.setNombre("Categoria 1");

        CategoriaDTO categoriaDTO = new CategoriaDTO();
        categoriaDTO.setId(1);
        categoriaDTO.setNombre("Categoria 1");

        when(repositorio.findById(1)).thenReturn(Optional.of(categoria));
        when(repositorio.save(any(Categoria.class))).thenReturn(categoria);

        CategoriaDTO result = categoriaService.actualizarCategoria(categoriaDTO, null);

        assertThat(result).isNotNull();
        assertThat(result.getNombre()).isEqualTo("Categoria 1");
    }

    @Test
    void testBorrarCategoria() throws Exception {
        Categoria categoria = new Categoria();
        categoria.setId(1);

        when(repositorio.findById(1)).thenReturn(Optional.of(categoria));

        categoriaService.borrarCategoria(1);

    }

    @Test
    void testGetCategoria() throws Exception {
        Categoria categoria = new Categoria();
        categoria.setId(1);
        categoria.setNombre("Categoria 1");

        when(repositorio.findById(1)).thenReturn(Optional.of(categoria));

        CategoriaDTO result = categoriaService.getCategoria(1);

        assertThat(result).isNotNull();
        assertThat(result.getNombre()).isEqualTo("Categoria 1");
    }

    @Test
    void testGetCategoriaPorAlias() throws Exception {
        String alias = "categoria-alias";
        Categoria categoria = new Categoria();
        categoria.setAlias(alias);

        when(repositorio.buscarActivaPorAlias(alias)).thenReturn(categoria);

        CategoriaDTO result = categoriaService.getCategoriaPorAlias(alias);

        assertThat(result).isNotNull();
        assertThat(result.getAlias()).isEqualTo(alias);
    }

    @Test
    void testGetCategoriaRaiz() {
        Categoria categoria = new Categoria();
        categoria.setId(1);
        categoria.setNombre("Categoria 1");

        CategoriaDTO categoriaDTO = new CategoriaDTO();
        categoriaDTO.setId(1);
        categoriaDTO.setNombre("Categoria 1");

        when(repositorio.findById(1)).thenReturn(Optional.of(categoria));

        List<CategoriaDTO> result = categoriaService.getCategoriaRaiz(categoriaDTO);

        assertThat(result).isNotNull();
    }

    @Test
    void testExisteCategoriaPorNombre() {
        String nombre = "Categoria 1";
        when(repositorio.buscarPorNombre(nombre)).thenReturn(new Categoria());

        boolean exists = categoriaService.existeCategoriaPorNombre(nombre);

        assertThat(exists).isTrue();
    }

    @Test
    void testExisteCategoriaPorAlias() {
        String alias = "categoria-alias";
        when(repositorio.findByAlias(alias)).thenReturn(new Categoria());

        boolean exists = categoriaService.existeCategoriaPorAlias(alias);

        assertThat(exists).isTrue();
    }

    @Test
    void testListarPorPagina() {
        Categoria categoria = new Categoria();
        categoria.setId(1);
        categoria.setNombre("Categoria 1");

        Pageable pageable = PageRequest.of(0, 10);
        Page<Categoria> page = new PageImpl<>(Arrays.asList(categoria), pageable, 1);

        when(repositorio.buscarCategoriasPadre(pageable)).thenReturn(page);

        Paginacion<CategoriaDTO> result = categoriaService.listarPorPagina(1);

        assertThat(result).isNotNull();
    }

    @Test
    void testGetEstadistica() {
        EstadisticaDTO estadistica = new EstadisticaDTO();

        categoriaService.getEstadistica(estadistica);

    }
}
