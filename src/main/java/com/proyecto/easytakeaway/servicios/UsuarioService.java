package com.proyecto.easytakeaway.servicios;

import com.proyecto.easytakeaway.dto.EstadisticaDTO;
import com.proyecto.easytakeaway.dto.Paginacion;
import com.proyecto.easytakeaway.dto.UsuarioDTO;
import com.proyecto.easytakeaway.excepciones.UsuarioException;

import java.util.List;

public interface UsuarioService {
    public static final int USUARIO_POR_PAGINA = 4;

    UsuarioDTO findByUsuario(String usuario);

    List<UsuarioDTO> findAllUsers();

    void registrarUsuario(UsuarioDTO usuarioDTO);

    void guardarUsuario(UsuarioDTO usuarioDTO);

    UsuarioDTO getUsuario(Integer id) throws UsuarioException;

    void borrarUsuario(Integer id) throws UsuarioException;

    Paginacion<UsuarioDTO> listarPorPagina(int numeroPagina);

    String existeUsuario(String id, String password);

    void getEstadistica(EstadisticaDTO estadisticas);
}
