package com.proyecto.easytakeaway;

import static org.mockito.Mockito.when;
import static org.assertj.core.api.Assertions.assertThat;

import com.proyecto.easytakeaway.modelos.Rol;
import com.proyecto.easytakeaway.modelos.Usuario;
import com.proyecto.easytakeaway.repositorios.UsuarioRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;


@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@ExtendWith(MockitoExtension.class)
public class UsuarioRepositoryTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Test
    public void testFindByLogin() {
        String username = "testUser";
        Usuario usuario = new Usuario();
        usuario.setLogin(username);

        when(usuarioRepository.findByLogin(username)).thenReturn(usuario);

        Usuario found = usuarioRepository.findByLogin(username);

        assertThat(found.getLogin()).isEqualTo(username);
    }

    @Test
    public void testCountByUsuarioID() {
        Integer userId = 1;
        Long expectedCount = 5L;

        when(usuarioRepository.countByUsuarioID(userId)).thenReturn(expectedCount);

        Long count = usuarioRepository.countByUsuarioID(userId);

        assertThat(count).isEqualTo(expectedCount);
    }

    @Test
    public void testCountByRol() {
        Rol rol = new Rol();
        rol.setRolID(1);
        Long expectedCount = 10L;

        when(usuarioRepository.countByRol(rol)).thenReturn(expectedCount);

        Long count = usuarioRepository.countByRol(rol);

        assertThat(count).isEqualTo(expectedCount);
    }

    @Test
    public void testContarUsuariosSinPedidos() {
        Long expectedCount = 0L;

        when(usuarioRepository.contarUsuariosSinPedidos()).thenReturn(expectedCount);

        Long count = usuarioRepository.contarUsuariosSinPedidos();

        assertThat(count).isGreaterThanOrEqualTo(expectedCount);
    }

    @Test
    public void testContarUsuariosSinConfirmarPedido() {
        Long expectedCount = 0L;

        when(usuarioRepository.contarUsuariosSinConfirmarPedido()).thenReturn(expectedCount);

        Long count = usuarioRepository.contarUsuariosSinConfirmarPedido();

        assertThat(count).isGreaterThanOrEqualTo(expectedCount);
    }
}
