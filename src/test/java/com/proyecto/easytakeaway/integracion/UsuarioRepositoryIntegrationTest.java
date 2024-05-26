package com.proyecto.easytakeaway.integracion;
import static org.assertj.core.api.Assertions.assertThat;

import com.proyecto.easytakeaway.modelos.Rol;
import com.proyecto.easytakeaway.modelos.Usuario;
import com.proyecto.easytakeaway.repositorios.RolRepository;
import com.proyecto.easytakeaway.repositorios.UsuarioRepository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@ExtendWith(SpringExtension.class)
@Transactional
public class UsuarioRepositoryIntegrationTest {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private RolRepository rolRepository;

    @Test
    void testCrearUsuario() {
        Rol rol = rolRepository.findByNombre("usuario");

        Usuario usuario = new Usuario();
        usuario.setLogin("testuser");
        usuario.setPassword("testpassword");
        usuario.setRol(rol);

        Usuario savedUsuario = usuarioRepository.save(usuario);

        assertThat(savedUsuario).isNotNull();
        assertThat(savedUsuario.getUsuarioID()).isNotNull();
        assertThat(savedUsuario.getLogin()).isEqualTo("testuser");
        assertThat(savedUsuario.getPassword()).isEqualTo("testpassword");
        assertThat(savedUsuario.getRol()).isEqualTo(rol);

        Usuario foundUsuario = usuarioRepository.findByLogin("testuser");
        assertThat(foundUsuario).isNotNull();
        assertThat(foundUsuario.getUsuarioID()).isEqualTo(savedUsuario.getUsuarioID());
    }
}

