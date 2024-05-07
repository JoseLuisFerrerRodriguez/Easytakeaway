package com.proyecto.easytakeaway.repositorios;


import com.proyecto.easytakeaway.modelos.Producto;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;


public interface ProductoRepository extends JpaRepository<Producto, Integer> {

    List<Producto> findAllByCategoriaId(Integer category);

    public Producto findByAlias(String alias);

    public Producto findByNombre(String nombre);

    public Long countById(Integer id);

    @Query(nativeQuery=true, value = "SELECT count(productoID) FROM productos WHERE categoriaID = (?1) ")
    public int countByCategoria(Integer id);

    @Query(nativeQuery=true, value = "SELECT count(productoID) FROM lineasPedido WHERE productoID = (?1) ")
    public int countByProductoEnLineasPedido(Integer id);

    @Query(nativeQuery=true, value="SELECT * FROM productos ORDER BY RAND() LIMIT 8")
    public List<Producto> findRandom();

    @Query("SELECT p FROM Producto p "
            + "WHERE (p.categoria.id = ?1 OR p.categoria.todosPadresIDs LIKE %?2%) "
            + "ORDER BY p.nombre ASC")
    public Page<Producto> obtenerPorCategoria(Integer categoriaId, Pageable pageable, String categoriaIDMatch);

    @Query("SELECT p FROM Producto p "
            + "WHERE (p.categoria.id in ?1 OR p.categoria.todosPadresIDs LIKE %?2%) "
            + "ORDER BY p.nombre ASC")
    public Page<Producto> obtenerPorCategorias(List<Integer> categoriasId, Pageable pageable, String categoriaIDMatch);

    @Query(nativeQuery = true, value = "SELECT * FROM productos WHERE MATCH(nombre, descripcion) AGAINST (?1)")
    public Page<Producto> search(String keyword, Pageable pageable);

    @Query("SELECT p FROM Producto p WHERE p.nombre LIKE %?1% "
            + "OR p.descripcion LIKE %?1% "
            + "OR p.categoria.nombre LIKE %?1%")
    public Page<Producto> buscarTodos(String keyword, Pageable pageable);

    @Query("SELECT p FROM Producto p WHERE p.categoria.id = ?1 " +
            "OR p.categoria.todosPadresIDs LIKE %?2%")
    public Page<Producto> buscarTodosEnCategoria(Integer categoryId, String categoryIdMatch, Pageable pageable);

    @Query("SELECT p FROM Producto p WHERE (p.categoria.id = ?1 " +
            "OR p.categoria.todosPadresIDs LIKE %?2%) AND "
            + "(p.nombre LIKE %?3% "
            + "OR p.descripcion LIKE %?3% "
            + "OR p.categoria.nombre LIKE %?3%)")
    public Page<Producto> buscarEnCategoria(Integer categoryId, String categoryIdMatch, String keyword, Pageable pageable);


    @Query("SELECT p.nombre FROM Producto p " +
            "WHERE p.id IN " +
                "(SELECT lp.producto.id FROM LineaPedido lp " +
                    "WHERE lp.pedido IS NOT NULL " +
                    "GROUP BY lp.producto.id " +
                    "ORDER BY SUM(lp.cantidad) DESC)")
    List<String> obtenerProductosMasVendidos();


}
