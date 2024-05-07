package com.proyecto.easytakeaway.repositorios;

import com.proyecto.easytakeaway.modelos.Rol;
import com.proyecto.easytakeaway.modelos.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {

    public Usuario findByLogin(String username);

    public Long countByUsuarioID(Integer id);

    public Long countByRol(Rol rol);

    @Query("SELECT COUNT(u) FROM Usuario u WHERE u.usuarioID NOT IN (SELECT DISTINCT p.usuario.usuarioID FROM Pedido p)")
    public Long contarUsuariosSinPedidos();

    @Query("SELECT COUNT(DISTINCT lp.usuario.usuarioID) FROM LineaPedido lp WHERE lp.pedido.id IS NULL")
    public Long contarUsuariosSinConfirmarPedido();
}
