package com.proyecto.easytakeaway.repositorios;

import com.proyecto.easytakeaway.modelos.Categoria;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CategoriaRepository extends JpaRepository<Categoria, Integer> {
    @Query("SELECT Categoria FROM Categoria Categoria WHERE Categoria.activado = true ORDER BY Categoria.nombre ASC")
    public List<Categoria> buscarActivas();

    @Query("select Categoria from Categoria Categoria where Categoria.activado = true and Categoria.alias = ?1")
    public Categoria buscarActivaPorAlias(String alias);

    public Categoria findByAlias(String alias);

    public Long countById(Integer id);

    public Long countByActivado(Boolean activado);

    @Query("SELECT c FROM Categoria c WHERE c.nombre = :nombre")
    public Categoria buscarPorNombre(@Param("nombre") String nombre);

    @Query("SELECT c FROM Categoria c WHERE c.padre.id is NULL")
    public List<Categoria> buscarCategoriasPadre();

    @Query("SELECT c FROM Categoria c WHERE c.padre.id is NULL")
    public Page<Categoria> buscarCategoriasPadre(Pageable pageable);

    @Query("SELECT c.nombre, COUNT(p) FROM Categoria c JOIN c.productos p GROUP BY c.id")
    public List<Object[]> contarProductosPorCategoria();
}
