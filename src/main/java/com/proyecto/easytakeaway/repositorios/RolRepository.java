package com.proyecto.easytakeaway.repositorios;

import com.proyecto.easytakeaway.modelos.Rol;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RolRepository extends JpaRepository<Rol, Integer> {

    Rol findByNombre(String nombre);
}
