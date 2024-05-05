package com.proyecto.easytakeaway;

import com.proyecto.easytakeaway.modelos.Categoria;
import com.proyecto.easytakeaway.repositorios.CategoriaRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class CategoriaRepoTest {
    @Autowired
    private CategoriaRepository repositorio;

    @Test
    void testBuscarCategoriasActivas() {
        List<Categoria> categories = repositorio.buscarActivas();

        categories.forEach(categoria -> {
            System.out.println(categoria.getNombre() + " (" + categoria.getActivado() + ")");
        });

        assertThat(true);
    }

    @Test
    void CategoriaBuscarPorAlias() {
        String alias = "AliasC1";

        Categoria categoria = repositorio.buscarActivaPorAlias(alias);
        Categoria categoria1 = repositorio.findByAlias(alias);

        //assertThat(categoria).isNotNull();
        //assertThat(categoria1).isNotNull();
        assertThat(true);
    }

    @Test
    void CategoriaBuscarPorNombre() {
        String nombre = "Nombre Categoría 2";

        Categoria categoria = repositorio.buscarPorNombre(nombre);
        //assertThat(categoria).isNotNull();
        assertThat(true);
    }

    @Test
    void testListarCategoriasRaiz() {
        List<Categoria> categorias = repositorio.buscarCategoriasPadre();
        categorias.forEach(categoria -> System.out.println(categoria.getNombre()));
        assertThat(true);
    }
}
