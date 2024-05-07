package com.proyecto.easytakeaway.servicios.impl;

import com.proyecto.easytakeaway.dto.EstadisticaDTO;
import com.proyecto.easytakeaway.dto.Paginacion;
import com.proyecto.easytakeaway.dto.UsuarioDTO;
import com.proyecto.easytakeaway.excepciones.UsuarioException;
import com.proyecto.easytakeaway.modelos.Rol;
import com.proyecto.easytakeaway.modelos.Usuario;
import com.proyecto.easytakeaway.repositorios.RolRepository;
import com.proyecto.easytakeaway.repositorios.UsuarioRepository;
import com.proyecto.easytakeaway.servicios.PedidosService;
import com.proyecto.easytakeaway.servicios.SeguridadService;
import com.proyecto.easytakeaway.servicios.UsuarioService;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class UsuarioServiceImpl implements UsuarioService {

    @Autowired
    private SeguridadService seguridadService;
    @Autowired
    private UsuarioRepository userRepository;
    @Autowired
    private RolRepository roleRepository;
    @Autowired
    private PedidosService pedidosService;

    @Override
    public UsuarioDTO findByUsuario(String username) {
        log.info("findByUsuario: "+username);
        Usuario usuario = userRepository.findByLogin(username);
        return (usuario != null) ? usuario.convertirModeloaDTO() : null;
    }

    @Override
    public List<UsuarioDTO> findAllUsers() {
        log.info("findAllUsers: ");
        List<Usuario> users = userRepository.findAll();
        return users.stream().map((user) -> user.convertirModeloaDTO()).collect(Collectors.toList());
    }

    @Override
    public void registrarUsuario(UsuarioDTO usuarioDTO) {
        log.info("Registrar usuario: "+usuarioDTO);

        usuarioDTO.setPassword(codificarContraseña(usuarioDTO.getPassword()));

        Usuario usuario = usuarioDTO.convertirDTOaModelo();

        Rol rol = roleRepository.findByNombre("usuario");
        usuario.setRol(rol);

        userRepository.save(usuario);
    }

    @Override
    public void guardarUsuario(UsuarioDTO usuarioDTO)  {
        log.info("guardarUsuario: " +usuarioDTO);

        boolean actualizado = (usuarioDTO.getId() != null);

        log.info(!actualizado?"Nuevo":"Actualizacion");

        if (actualizado) {
            Optional<Usuario> usuarioContainer = userRepository.findById(usuarioDTO.getId());
            Usuario existente = usuarioContainer.get();
            if (usuarioDTO.getPassword().isEmpty()) {
                usuarioDTO.setPassword(existente.getPassword());
            } else {
                usuarioDTO.setPassword(codificarContraseña(usuarioDTO.getPassword()));
            }
        } else {
            usuarioDTO.setPassword(codificarContraseña(usuarioDTO.getPassword()));
        }

        if(usuarioDTO.getRolID() == null) {
            Rol rol = roleRepository.findByNombre(usuarioDTO.getRol());
            if(rol != null) {
                usuarioDTO.setRolID(rol.getRolID());
            }
        }

        Usuario usuario = usuarioDTO.convertirDTOaModelo();
        userRepository.save(usuario);
    }

    @Override
    public UsuarioDTO getUsuario(Integer id) throws UsuarioException {
        log.info("getUsuario: "+id);
        try {
            Optional<Usuario> usuario = userRepository.findById(id);
            return usuario.get().convertirModeloaDTO();
        } catch (NoSuchElementException ex) {
            log.error("No se ha encontrado :" + ex.getMessage());
            throw new UsuarioException(UsuarioException.NO_EXISTE);
        }
    }

    @Override
    public void borrarUsuario(Integer id) throws UsuarioException {
        log.info("borrarUsuario: "+id);

        Usuario usuario = null;
        try {
            usuario = userRepository.getReferenceById(id);
        } catch (EntityNotFoundException e) {
            log.error("No se ha encontrado :" + e.getMessage());
            throw new UsuarioException(UsuarioException.NO_EXISTE);
        }

        if(usuario == null) {
            throw new UsuarioException(UsuarioException.NO_EXISTE);
        }

        if(usuario.getUsuarioID()!=null && usuario.getUsuarioID().intValue()==1) {
            throw new UsuarioException(UsuarioException.NO_ADMIN_USUARIO);
        }

        if(pedidosService.tieneLineasPedidoUsuario(id)) {
            throw new UsuarioException(UsuarioException.ERROR_BORRAR_TIENE_LINEAS_PEDIDOS);
        }

        userRepository.deleteById(id);
    }

    @Override
    public Paginacion<UsuarioDTO> listarPorPagina(int numeroPagina) {
        log.info("listarPorPagina: "+numeroPagina);
        Pageable pageable = PageRequest.of(numeroPagina - 1, USUARIO_POR_PAGINA);

        Page<Usuario> pagina = userRepository.findAll(pageable);

        long inicio = (numeroPagina - 1) * USUARIO_POR_PAGINA + 1;
        long fin = inicio + USUARIO_POR_PAGINA - 1;

        Paginacion<UsuarioDTO> paginacion = new Paginacion<UsuarioDTO>();
        paginacion.setInicio(inicio);
        paginacion.setFin(fin);
        paginacion.setTotalPaginas(pagina.getTotalPages());
        paginacion.setTotalElementos(pagina.getTotalElements());

        List<UsuarioDTO> lista = new ArrayList<UsuarioDTO>();

        List<Usuario> listadoModelo = pagina.getContent();

        for (int i = 0; i < listadoModelo.size(); i++) {
            Usuario usuario =  listadoModelo.get(i);
            lista.add(usuario.convertirModeloaDTO());
        }

        paginacion.setElementos(lista);

        return paginacion;
    }

    @Override
    public String existeUsuario(String login, String password) {
        log.info("existeUsuario: "+login);
        Usuario usuario = userRepository.findByLogin(login);

        if(usuario == null) {
            return "El usuario no existe";
        }

        if(!seguridadService.validarContraseña(password, usuario.getPassword())) {
            return "La contraseña es incorrecta";
        }

        return "OK";
    }

    @Override
    public void getEstadistica(EstadisticaDTO estadisticas) {

        Long totalUsuarios = userRepository.count();
        Long usuariosSinPedidos = userRepository.contarUsuariosSinPedidos();
        Long usuariosSinConfirmarPedido = userRepository.contarUsuariosSinConfirmarPedido();

        Map<String, Integer> usuariosPermisos = new HashMap<>();

        List<Rol> roles = roleRepository.findAll();
        for (Rol role : roles) {
           long total = userRepository.countByRol(role);
           usuariosPermisos.put(role.getNombre(), ((int) total));
        }

        estadisticas.setTotalUsuarios(totalUsuarios==null?0:totalUsuarios);
        estadisticas.setUsuariosSinpedidos(usuariosSinPedidos==null?0:usuariosSinPedidos);
        estadisticas.setUsuariosSinConfirmarPedido(usuariosSinConfirmarPedido==null?0:usuariosSinConfirmarPedido);
        estadisticas.setUsuariosPermisos(usuariosPermisos);

    }

    private String codificarContraseña(String contraseña) {
        return seguridadService.codificarContraseña(contraseña);
    }


}
