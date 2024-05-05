package com.proyecto.easytakeaway.dto;

import com.proyecto.easytakeaway.modelos.Categoria;
import jakarta.persistence.Column;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CategoriaDTO {

    private Integer id;

    @Size(max = 100)
    @NotNull
    private String nombre;

    @Size(max = 100)
    @NotNull
    private String alias;

    @Size(max = 250)
    private String descripcion;

    @Size(max = 255)
    private String imagenURL;

    private Boolean activado;

    private CategoriaDTO padre;

    private Set<CategoriaDTO> hijos = new HashSet<>();

    private Boolean esNueva;

    private String todosPadresIDs;


    public Categoria convertirDTOaModelo() {
        Categoria categoria = new Categoria();
        categoria.setId(this.getId());
        categoria.setNombre(this.getNombre());
        categoria.setAlias(this.getAlias());
        categoria.setDescripcion(this.getDescripcion());
        categoria.setImagenURL(this.getImagenURL());
        categoria.setActivado(this.getActivado());
        categoria.setTodosPadresIDs(this.getTodosPadresIDs());

        if(this.getPadre() != null)
            categoria.setPadre(this.getPadre().convertirDTOaModelo());

        if(this.getHijos() != null) {

            if(this.getHijos() != null) {
               Set<Categoria> hijos = new HashSet<>();

                for (CategoriaDTO catDTO : this.getHijos()) {
                    if(catDTO != null ) {
                        Categoria cat = new Categoria();
                        cat.setId(catDTO.getId());
                        hijos.add(cat);
                    }
                }
                categoria.setHijos(hijos);
            }

        }

        return categoria;
    }

}
