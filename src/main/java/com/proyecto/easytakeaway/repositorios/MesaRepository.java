package com.proyecto.easytakeaway.repositorios;

import com.proyecto.easytakeaway.modelos.Categoria;
import com.proyecto.easytakeaway.modelos.Mesa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MesaRepository extends JpaRepository<Mesa, Integer> {


    Long countById(Integer id);

    Mesa findByNumeroMesa(Integer numeroMesa);
}
