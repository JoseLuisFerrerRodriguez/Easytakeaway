package com.proyecto.easytakeaway;

import static org.mockito.Mockito.when;
import static org.assertj.core.api.Assertions.assertThat;

import com.proyecto.easytakeaway.modelos.Categoria;
import com.proyecto.easytakeaway.repositorios.CategoriaRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Arrays;
import java.util.List;

@ExtendWith(MockitoExtension.class)
public class CategoriaRepositoryTest {

    @Mock
    private CategoriaRepository categoriaRepository;

    @Test
    public void testBuscarActivas() {
        Categoria categoria1 = new Categoria();
        categoria1.setNombre("Categoria 1");
        categoria1.setActivado(true);

        Categoria categoria2 = new Categoria();
        categoria2.setNombre("Categoria 2");
        categoria2.setActivado(true);

        List<Categoria> categorias = Arrays.asList(categoria1, categoria2);
        when(categoriaRepository.buscarActivas()).thenReturn(categorias);

        List<Categoria> result = categoriaRepository.buscarActivas();
        assertThat(result).hasSize(2).contains(categoria1, categoria2);
    }

    @Test
    public void testBuscarActivaPorAlias() {
        String alias = "categoria-alias";
        Categoria categoria = new Categoria();
        categoria.setAlias(alias);
        categoria.setActivado(true);

        when(categoriaRepository.buscarActivaPorAlias(alias)).thenReturn(categoria);

        Categoria found = categoriaRepository.buscarActivaPorAlias(alias);
        assertThat(found.getAlias()).isEqualTo(alias);
    }

    @Test
    public void testFindByAlias() {
        String alias = "categoria-alias";
        Categoria categoria = new Categoria();
        categoria.setAlias(alias);

        when(categoriaRepository.findByAlias(alias)).thenReturn(categoria);

        Categoria found = categoriaRepository.findByAlias(alias);

        assertThat(found.getAlias()).isEqualTo(alias);
    }

    @Test
    public void testCountById() {
        Integer id = 1;
        Long expectedCount = 5L;

        when(categoriaRepository.countById(id)).thenReturn(expectedCount);

        Long count = categoriaRepository.countById(id);

        assertThat(count).isEqualTo(expectedCount);
    }

    @Test
    public void testCountByActivado() {
        Boolean activado = true;
        Long expectedCount = 10L;

        when(categoriaRepository.countByActivado(activado)).thenReturn(expectedCount);

        Long count = categoriaRepository.countByActivado(activado);

        assertThat(count).isEqualTo(expectedCount);
    }

    @Test
    public void testBuscarPorNombre() {
        String nombre = "Categoria 1";
        Categoria categoria = new Categoria();
        categoria.setNombre(nombre);

        when(categoriaRepository.buscarPorNombre(nombre)).thenReturn(categoria);

        Categoria found = categoriaRepository.buscarPorNombre(nombre);

        assertThat(found.getNombre()).isEqualTo(nombre);
    }

    @Test
    public void testBuscarCategoriasPadre() {
        Categoria categoria1 = new Categoria();
        categoria1.setNombre("Categoria Padre 1");

        Categoria categoria2 = new Categoria();
        categoria2.setNombre("Categoria Padre 2");

        List<Categoria> categorias = Arrays.asList(categoria1, categoria2);

        when(categoriaRepository.buscarCategoriasPadre()).thenReturn(categorias);

        List<Categoria> result = categoriaRepository.buscarCategoriasPadre();

        assertThat(result).hasSize(2).contains(categoria1, categoria2);
    }

    @Test
    public void testBuscarCategoriasPadrePageable() {
        Categoria categoria1 = new Categoria();
        categoria1.setNombre("Categoria Padre 1");

        Pageable pageable = PageRequest.of(0, 10);
        Page<Categoria> page = new PageImpl<>(Arrays.asList(categoria1), pageable, 1);

        when(categoriaRepository.buscarCategoriasPadre(pageable)).thenReturn(page);

        Page<Categoria> result = categoriaRepository.buscarCategoriasPadre(pageable);

        assertThat(result.getContent()).hasSize(1).contains(categoria1);
    }

    @Test
    public void testContarProductosPorCategoria() {
        Object[] categoriaProductoCount = new Object[]{"Categoria 1", 5L};
        List<Object[]> expectedCount = Arrays.<Object[]>asList(categoriaProductoCount);

        when(categoriaRepository.contarProductosPorCategoria()).thenReturn(expectedCount);

        List<Object[]> result = categoriaRepository.contarProductosPorCategoria();

        assertThat(result).hasSize(1).contains(categoriaProductoCount);
    }
}
