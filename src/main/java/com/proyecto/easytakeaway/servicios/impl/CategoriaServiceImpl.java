package com.proyecto.easytakeaway.servicios.impl;

import com.proyecto.easytakeaway.configuracion.AppConstants;
import com.proyecto.easytakeaway.dto.CategoriaDTO;
import com.proyecto.easytakeaway.dto.EstadisticaDTO;
import com.proyecto.easytakeaway.dto.Paginacion;
import com.proyecto.easytakeaway.excepciones.AplicationIException;
import com.proyecto.easytakeaway.modelos.Categoria;
import com.proyecto.easytakeaway.servicios.CategoriaService;
import com.proyecto.easytakeaway.repositorios.CategoriaRepository;
import com.proyecto.easytakeaway.excepciones.CategoriaException;
import com.proyecto.easytakeaway.servicios.FicheroService;
import com.proyecto.easytakeaway.servicios.ProductoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;

@Slf4j
@Service
public class CategoriaServiceImpl implements CategoriaService {
    @Autowired
    private CategoriaRepository repositorio;

    @Autowired
    private ProductoService productoService;

    @Autowired
    private FicheroService ficheroService;

    @Override
    public List<CategoriaDTO> obtenerCategoriasActivas() {
        log.info("obtenerCategoriasActivas");
        List<Categoria> listadoModelo = repositorio.buscarActivas();
        List<CategoriaDTO> lista = new ArrayList<>();

        for (int i = 0; i < listadoModelo.size(); i++) {
            lista.add(listadoModelo.get(i).convertirModeloaDTO());
        }

        log.info(lista.toString());
        return lista;
    }

    @Override
    public List<CategoriaDTO> obtenerTodas() {
        log.info("obtenerTodas");
        List<CategoriaDTO> categorias = new ArrayList<CategoriaDTO>();

        Iterable<Categoria> categoriesInDB = repositorio.findAll();

        for (Categoria categoria : categoriesInDB) {
            if (categoria.getPadre() == null) {
                categorias.add(categoria.convertirModeloaDTO());

                Set<Categoria> children = categoria.getHijos();

                for (Categoria subCategoria : children) {
                    CategoriaDTO subCategoriaDTO = subCategoria.convertirModeloaDTO();
                    categorias.add(subCategoriaDTO);
                    categorias.addAll(listaJerarquicaSubCategorias(subCategoria.getHijos()));
                }
            }
        }
        log.info(categorias.toString());
        return categorias;
    }

    @Transactional
    @Override
    public CategoriaDTO guardarCategoria(CategoriaDTO categoriaDTO, MultipartFile imagen) throws CategoriaException {

        log.info("guardarCategoria:" +categoriaDTO);

        CategoriaDTO parent = categoriaDTO.getPadre();
        if (parent != null && parent.getId() != null) {
            Categoria catPadre = repositorio.getReferenceById(parent.getId());
            String allParentIds = catPadre.getTodosPadresIDs() == null ? "-" : catPadre.getTodosPadresIDs();
            allParentIds += String.valueOf(parent.getId()) + "-";
            categoriaDTO.setTodosPadresIDs(allParentIds);
        }
        if (categoriaDTO.getAlias() == null || categoriaDTO.getAlias().isEmpty()) {
            String defaultAlias = categoriaDTO.getNombre().toLowerCase();
        } else {
            categoriaDTO.setAlias(categoriaDTO.getAlias().replaceAll(" ", "_").toLowerCase());
        }

        Categoria categoria = repositorio.save(categoriaDTO.convertirDTOaModelo());

        if(imagen != null && !imagen.isEmpty() ) {
            try {
                String path = AppConstants.URL_IMAGENES_CATEGORIAS;

                String extension = ficheroService.obtenerExtension(imagen);
                String nombreFichero = categoria.getId() + "." + extension;
                categoria.setImagenURL(nombreFichero);

                if(ficheroService.existeImagen(path, categoria.getImagenURL())) {
                    if(!categoria.getImagenURL().equalsIgnoreCase("default.png"))
                        ficheroService.borrarImagen(path, categoriaDTO.getImagenURL());
                }
                ficheroService.guardarImagen(path, imagen, categoria.getImagenURL());

                // Se guarda de nuevo la imagen porque no teniamos el id para la imagen
                repositorio.save(categoria);
            } catch (IOException e) {
                log.error("Error al borrar o guardar la imagen. " + e.getMessage());
                throw new CategoriaException(CategoriaException.ERROR_GUARDAR);
            }
        }

        return categoria.convertirModeloaDTO();
    }

    @Transactional
    @Override
    public CategoriaDTO actualizarCategoria(CategoriaDTO categoriaDTO, MultipartFile imagen) throws CategoriaException {
        log.info("actualizarCategoria:" +categoriaDTO);
        try {
            Categoria anterior = repositorio.getReferenceById(categoriaDTO.getId());

            CategoriaDTO parent = categoriaDTO.getPadre();
            if (parent != null && parent.getId() != null) {
                Categoria catPadre = repositorio.getReferenceById(parent.getId());
                String allParentIds = catPadre.getTodosPadresIDs() == null ? "-" : catPadre.getTodosPadresIDs();
                allParentIds += String.valueOf(parent.getId()) + "-";
                categoriaDTO.setTodosPadresIDs(allParentIds);
                categoriaDTO.setPadre(catPadre.convertirModeloaDTO());
            }

            if (categoriaDTO.getAlias() == null || categoriaDTO.getAlias().isEmpty()) {
                String defaultAlias = categoriaDTO.getNombre().toLowerCase();
            } else {
                categoriaDTO.setAlias(categoriaDTO.getAlias().replaceAll(" ", "_").toLowerCase());
            }

            anterior.setNombre(categoriaDTO.getNombre());
            anterior.setAlias(categoriaDTO.getAlias());
            anterior.setDescripcion(categoriaDTO.getDescripcion());
            anterior.setPadre(categoriaDTO.getPadre()!=null?categoriaDTO.getPadre().convertirDTOaModelo():null);
            anterior.setImagenURL(categoriaDTO.getImagenURL()!=null?categoriaDTO.getImagenURL():anterior.getImagenURL());
            anterior.setActivado(categoriaDTO.getActivado());

            Categoria categoria = repositorio.save(anterior);

            // Despues de guardar cambiamos la imagen por si va mal hace rollback
            if(imagen != null && !imagen.isEmpty()) {
                String path = AppConstants.URL_IMAGENES_CATEGORIAS;

                try {
                    if(ficheroService.existeImagen(path, categoria.getImagenURL())) {
                        if(!categoria.getImagenURL().equalsIgnoreCase("default.png"))
                            ficheroService.borrarImagen(path, categoria.getImagenURL());
                    }

                    String extension = ficheroService.obtenerExtension(imagen);
                    String nombreFichero = categoria.getId() + "." + extension;
                    categoria.setImagenURL(nombreFichero);

                    ficheroService.guardarImagen(path, imagen, nombreFichero);

                } catch (IOException e) {
                    log.error("Error al borrar o guardar la imagen. " + e.getMessage());
                    throw new CategoriaException(CategoriaException.ERROR_ACTUALIZAR);
                }
            }

            return categoria.convertirModeloaDTO();

        } catch (Exception e) {
            log.error("Error al actualizar la categoria. " + e.getMessage());
            throw new CategoriaException(CategoriaException.ERROR_ACTUALIZAR);
        }
    }

    @Transactional
    @Override
    public void borrarCategoria(int id) throws CategoriaException {
        log.info("borrar categoria:" +id);
        Categoria categoria = repositorio.getReferenceById(id);

        if(categoria == null) {
            log.error("Validacion. No existe ninguna categoria con ese id");
            throw new CategoriaException(CategoriaException.NO_EXISTE);
        }

        for (Categoria hijo : categoria.getHijos()) {
            int productos = productoService.contarProductosPorCategoria(hijo.getId());
            if(productos > 0) {
                log.error("Validación. Hay productos en las subcategorias. "+hijo.getId());
                throw new CategoriaException(CategoriaException.ERROR_BORRAR_EXISTE_PRODUCTOS);
            }
        }

        try {
            if(!categoria.getImagenURL().equalsIgnoreCase("default.png")) {
                if(ficheroService.existeImagen(AppConstants.URL_IMAGENES_CATEGORIAS, categoria.getImagenURL())) {
                    ficheroService.borrarImagen(AppConstants.URL_IMAGENES_CATEGORIAS, categoria.getImagenURL());
                }
            }
        } catch (IOException e) {
            log.error("Error al borrar la imagen. " + e.getMessage());
            throw new CategoriaException(CategoriaException.ERROR_BORRAR);
        }

        repositorio.deleteById(id);
    }

    @Override
    public CategoriaDTO getCategoria(Integer id) throws CategoriaException {
        log.info("getCategoria:" +id);
        try {
            return repositorio.getReferenceById(id).convertirModeloaDTO();
        } catch (NoSuchElementException e) {
            throw new CategoriaException("No se ha encontrado ninguna categoria con el id: " + id);
        }
    }

    @Override
    public CategoriaDTO getCategoriaPorAlias(String alias) throws CategoriaException {
        log.info("getCategoriaPorAlias:" +alias);
        Categoria categoria = repositorio.buscarActivaPorAlias(alias);
        if (categoria == null) {
            throw new CategoriaException("No se ha encontrado ninguna categoria con el alias: " + alias);
        }
        return categoria.convertirModeloaDTO();
    }

    @Override
    public List<CategoriaDTO> getCategoriaRaiz(CategoriaDTO child) {
        log.info("getCategoriaRaiz:" +child);
        List<CategoriaDTO> listaPadres = new ArrayList<>();
        CategoriaDTO padre = child.getPadre();

        while (padre != null) {
            listaPadres.add(0, padre);
            padre = padre.getPadre();
        }
        listaPadres.add(child);

        return listaPadres;
    }

    @Override
    public boolean existeCategoriaPorNombre(String nombre) {
        log.info("existeCategoriaPorNombre:" +nombre);

        Categoria categoriaTitulo = repositorio.buscarPorNombre(nombre);

        if (categoriaTitulo != null) {
            return true;
        }

        return false;
    }

    @Override
    public boolean existeCategoriaPorAlias(String alias) {
        log.info("existeCategoriaPorAlias:" +alias);
        Categoria categoriaAlias = repositorio.findByAlias(alias);
        if (categoriaAlias != null) {
           return true;
        }
        return false;
    }

    @Override
    public Paginacion<CategoriaDTO> listarPorPagina(int numeroPagina) {
        log.info("listarPorPagina: "+numeroPagina);
        Pageable pageable = PageRequest.of(numeroPagina - 1, CATEGORIA_PRINCIPAL_POR_PAGINA);

        Page<Categoria> pageCategorias = repositorio.buscarCategoriasPadre(pageable);

        long inicio = (numeroPagina - 1) * CATEGORIA_PRINCIPAL_POR_PAGINA + 1;
        long fin = inicio + CATEGORIA_PRINCIPAL_POR_PAGINA - 1;

        Paginacion<CategoriaDTO> paginacion = new Paginacion<CategoriaDTO>();
        paginacion.setInicio(inicio);
        paginacion.setFin(fin);
        paginacion.setTotalPaginas(pageCategorias.getTotalPages());
        paginacion.setTotalElementos(pageCategorias.getTotalElements());

        List<Categoria> categoriasRaiz = pageCategorias.getContent();

        paginacion.setElementos(listaJerarquicaCategorias(categoriasRaiz));

        return paginacion;

    }

    @Override
    public void getEstadistica(EstadisticaDTO estadistica) {

        long categoriasTotales = repositorio.count();
        long categoriasActivas = repositorio.countByActivado(true);
        long categoriasInactivas = repositorio.countByActivado(false);
        List<Object[]> productosPorCategoria = repositorio.contarProductosPorCategoria();

        Map<String, Long> productosPorCategoriaFormat = new LinkedHashMap<>();

        for (Object[] vt : productosPorCategoria) {
            productosPorCategoriaFormat.put((String)vt[0],(long)vt[1]);
        }
        estadistica.setProductosPorCategorias(productosPorCategoriaFormat);

        estadistica.setTotalCategorias(categoriasTotales);
        estadistica.setCategoriasActivas(categoriasActivas);
        estadistica.setCategoriasInactivas(categoriasInactivas);


    }

    private List<CategoriaDTO> listaJerarquicaCategorias(List<Categoria> listadoCategorias) {
        List<CategoriaDTO> jerarquia = new ArrayList<CategoriaDTO>();

        for (Categoria raiz : listadoCategorias) {
            jerarquia.add(raiz.convertirModeloaDTO());
            Set<Categoria> hijos = raiz.getHijos();

            for (Categoria subCategoria : hijos) {
                CategoriaDTO subCategoriaDTO = subCategoria.convertirModeloaDTO();
                //subCategoriaDTO.setPadre(raiz.convertirModeloaDTO());
                jerarquia.add(subCategoriaDTO);
                jerarquia.addAll(listaJerarquicaSubCategorias(subCategoria.getHijos()));
            }
        }
        return jerarquia;
    }

    private List<CategoriaDTO> listaJerarquicaSubCategorias(Set<Categoria> listadoSubcategorias) {
        List<CategoriaDTO> jerarquia = new ArrayList<CategoriaDTO>();

        for (Categoria raiz : listadoSubcategorias) {
            jerarquia.add(raiz.convertirModeloaDTO());
            Set<Categoria> hijos = raiz.getHijos();

            for (Categoria subCategoria : hijos) {
                CategoriaDTO subCategoriaDTO = subCategoria.convertirModeloaDTO();
                subCategoriaDTO.setPadre(raiz.getPadre().convertirModeloaDTO());

                jerarquia.add(subCategoriaDTO);
                jerarquia.addAll(listaJerarquicaSubCategorias(subCategoria.getHijos()));
            }
        }
        return jerarquia;
    }

}