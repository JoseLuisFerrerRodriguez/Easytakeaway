package com.proyecto.easytakeaway;

import com.proyecto.easytakeaway.modelos.Categoria;
import com.proyecto.easytakeaway.repositorios.CategoriaRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Rollback(value = false)
public class CategoriaTest {
    @Autowired
    private CategoriaRepository repositorio;

   /* @Test
    void testCrearCategoria() {
        Categoria nuevaCategoria = new Categoria();
        Categoria parent = repositorio.getReferenceById(1);
        nuevaCategoria.setNombre("Categoria 1");
        nuevaCategoria.setAlias("categoria_1");
        nuevaCategoria.setImagenURL("text.png");
        nuevaCategoria.setActivado(true);
        nuevaCategoria.setPadre(parent);

        Categoria saveCategoria = repositorio.save(nuevaCategoria);
        assertThat(saveCategoria).isNotNull();
        assertThat(saveCategoria.getId()).isPositive();
    }*/

   /* @Test
    void testBorrarCategoria() {
        Optional<Categoria> categoria = repositorio.findById(4);
        if (categoria.isPresent()) repositorio.delete(categoria.get());

        categoria = repositorio.findById(5);

        assertThat(categoria.isPresent());
    }*/

    @Test
    void testObtenerCategoria() {
        Categoria categoria = repositorio.getReferenceById(1);
        System.out.println("Categoria:");
        System.out.println("Categoria: " + categoria.getNombre());

        Set<Categoria> hijos = categoria.getHijos();
        System.out.println("Hijos:");
        hijos.stream().map(Categoria::getNombre).forEach(System.out::println);
        assertThat(hijos.size()).isGreaterThan(0);
    }

    @Test
    void testMostrarHerenciaCategorias() {
        Iterable<Categoria> categories = repositorio.findAll();
        for (Categoria Categoria : categories) {
            if (Categoria.getPadre() == null) {
                System.out.println("Categoria: " + Categoria.getNombre());
                System.out.println("Hijos:");
                Set<Categoria> children = Categoria.getHijos();
                for (Categoria subcategoria : children) {
                    System.out.println(subcategoria.getNombre());
                }
            }
        }
    }

}
