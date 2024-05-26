package com.proyecto.easytakeaway.integracion;

import static org.assertj.core.api.Assertions.assertThat;

import com.proyecto.easytakeaway.modelos.Rol;
import com.proyecto.easytakeaway.repositorios.RolRepository;
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
public class RolRepositoryIntegrationTest {

    @Autowired
    private RolRepository rolRepository;

    @Test
    void testFindByNombre() {
        Rol rol = new Rol();
        rol.setRolID(999);
        rol.setNombre("ADMIN");
        rolRepository.save(rol);

        Rol found = rolRepository.findByNombre("ADMIN");

        assertThat(found).isNotNull();
        assertThat(found.getNombre()).isEqualTo("ADMIN");
    }
}