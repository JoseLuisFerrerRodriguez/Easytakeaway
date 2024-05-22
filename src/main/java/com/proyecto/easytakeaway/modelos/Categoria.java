/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.proyecto.easytakeaway.modelos;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.proyecto.easytakeaway.dto.CategoriaDTO;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;
import jakarta.validation.constraints.Size;
import lombok.*;

/**
 *
 * @author Jose
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "Categorias")
public class Categoria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Size(max = 100)
    @Column(name = "nombre")
    private String nombre;

    @Size(max = 100)
    @Column(name = "alias")
    private String alias;

    @Size(max = 250)
    @Column(name = "descripcion")
    private String descripcion;

    @Size(max = 255)
    @Column(name = "imagen", nullable = true)
    private String imagenURL;

    @Column(name = "activado")
    private Boolean activado;
    
    @ManyToOne
    @JoinColumn(name = "padre_id", nullable = true)
    private Categoria padre;

    @OneToMany(mappedBy = "padre", cascade = CascadeType.REMOVE, fetch=FetchType.EAGER)
    @OrderBy("nombre asc")
    @ToString.Exclude
    private Set<Categoria> hijos = new HashSet<>();

    @OneToMany(cascade = {CascadeType.ALL, CascadeType.PERSIST}, mappedBy = "categoria")
    private List<Producto> productos;

    @Column(name = "todos_padres_ids", length = 255)
    @ToString.Exclude
    private String todosPadresIDs;

    @PrePersist
    void prePersist() {
        if (this.imagenURL == null)
            this.imagenURL = "default.png";
    }

    public Categoria(Integer id) {
        this.id = id;
    }

    public Categoria(String nombre) {
        this.nombre = nombre;
        this.alias = nombre;
        this.imagenURL = "default.png";
    }

    public Categoria(String nombre, Categoria padre) {
        this(nombre);
        this.padre = padre;
    }

    public CategoriaDTO convertirModeloaDTO(){
        CategoriaDTO categoriaDTO = new CategoriaDTO();

        categoriaDTO.setId(this.getId());
        categoriaDTO.setNombre(this.getNombre());
        categoriaDTO.setDescripcion(this.getDescripcion());
        categoriaDTO.setAlias(this.getAlias());
        categoriaDTO.setImagenURL(this.getImagenURL());
        categoriaDTO.setActivado(this.getActivado());
        categoriaDTO.setTodosPadresIDs(this.getTodosPadresIDs());

        if(this.getPadre() != null) {
            categoriaDTO.setPadre(this.getPadre().convertirModeloaDTO());
        }

        if(this.getHijos() != null) {
            Set<CategoriaDTO> hijosDTO = new HashSet<>();
            for (Categoria cat : this.getHijos()) {
                if(cat != null )
                    hijosDTO.add(convertirModeloDTOLazyHijos(cat));
            }
            categoriaDTO.setHijos(hijosDTO);
        }

        return categoriaDTO;
    }


    /**
     * Método para convertir el modelo al DTO sin hacer una carga completa
     * de los hijos del padre. Esto es necesario para evitar la recursividad completa
     * que el padre llame a convertir a los hijos y los hijos vuelvan a convertir a los padres
     * @param categoria Modelo categoria
     * @return DTO del modelo categoria
     */
    private CategoriaDTO convertirModeloDTOLazyHijos(Categoria categoria) {

        CategoriaDTO categoriaDTO = new CategoriaDTO();

        categoriaDTO.setId(categoria.getId());
        categoriaDTO.setNombre(categoria.getNombre());
        categoriaDTO.setDescripcion(categoria.getDescripcion());
        categoriaDTO.setAlias(categoria.getAlias());
        categoriaDTO.setImagenURL(categoria.getImagenURL());
        categoriaDTO.setActivado(categoria.getActivado());

        if(categoria.getPadre() != null) {
            Categoria padre = categoria.getPadre();
            CategoriaDTO padreDTO = new CategoriaDTO();
            padreDTO.setId(padre.getId());
            padreDTO.setNombre(padre.getNombre());
            padreDTO.setDescripcion(padre.getDescripcion());
            padreDTO.setAlias(padre.getAlias());
            padreDTO.setImagenURL(padre.getImagenURL());
            padreDTO.setActivado(padre.getActivado());
            padreDTO.setTodosPadresIDs(padre.getTodosPadresIDs());
            padreDTO.setHijos(null);

            categoriaDTO.setPadre(padreDTO);
        }

        if(this.getHijos() != null) {
            Set<CategoriaDTO> hijosDTO = new HashSet<>();
            for (Categoria cat : categoria.getHijos()) {
                if(cat != null )
                    hijosDTO.add(convertirModeloDTOLazyHijos(cat));
            }
            categoriaDTO.setHijos(hijosDTO);
        }

        return categoriaDTO;
    }

}
