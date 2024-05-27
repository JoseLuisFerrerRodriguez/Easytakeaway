package com.proyecto.easytakeaway.servicios.impl;


import com.proyecto.easytakeaway.configuracion.AppConstants;
import com.proyecto.easytakeaway.dto.MesaDTO;
import com.proyecto.easytakeaway.dto.Paginacion;
import com.proyecto.easytakeaway.excepciones.MesaException;
import com.proyecto.easytakeaway.excepciones.UsuarioException;
import com.proyecto.easytakeaway.modelos.*;
import com.proyecto.easytakeaway.repositorios.MesaRepository;
import com.proyecto.easytakeaway.servicios.FicheroService;
import com.proyecto.easytakeaway.servicios.MesaService;
import com.proyecto.easytakeaway.servicios.PedidosService;
import com.proyecto.easytakeaway.servicios.SeguridadService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Slf4j
@Service
public class MesaServiceImpl implements MesaService {

    @Autowired
    private MesaRepository mesaRepository;

    @Autowired
    private FicheroService ficheroService;

    @Autowired
    private SeguridadService seguridadService;

    @Autowired
    private PedidosService pedidosService;

    @Value("${urldomain}")
    private String url;

    @Override
    public List<MesaDTO> obtenerTodas() {
        log.info("obtenerTodas");
        List<Mesa> mesas = mesaRepository.findAll();
        List<MesaDTO> lista = new ArrayList<>();

        for (Mesa mesa : mesas) {
            lista.add(mesa.convertirModeloaDTO());
        }
        log.info(lista.toString());
        return lista;
    }

    @Override
    public Paginacion<MesaDTO> listarPorPagina(int numeroPagina) {
        log.info("listarPorPagina: "+numeroPagina);
        Pageable pageable = PageRequest.of(numeroPagina - 1, MESAS_POR_PAGINA);

        Page<Mesa> pagina = mesaRepository.findAll(pageable);

        long inicio = (numeroPagina - 1) * MESAS_POR_PAGINA + 1;
        long fin = inicio + MESAS_POR_PAGINA - 1;

        Paginacion<MesaDTO> paginacion = new Paginacion<MesaDTO>();
        paginacion.setInicio(inicio);
        paginacion.setFin(fin);
        paginacion.setTotalPaginas(pagina.getTotalPages());
        paginacion.setTotalElementos(pagina.getTotalElements());
        paginacion.setElementos(convertirDTOTodas(pagina.getContent()));

        return paginacion;
    }

    @Override
    public MesaDTO getMesa(Integer id) throws MesaException {
        log.info("getMesa: "+id);
        try {
            Optional<Mesa> usuario = mesaRepository.findById(id);
            return usuario.get().convertirModeloaDTO();
        } catch (NoSuchElementException ex) {
            throw new MesaException("No se ha encontrado ninguna mesa con el ID: " + id);
        }
    }


    @Override
    @Transactional
    public void guardar(MesaDTO mesaDTO) {
        log.info("guardarUsuario");
        log.info(!mesaDTO.getEsNueva()?"Nuevo":"Actualizacion");

        String value = seguridadService.codificarTexto(mesaDTO.getNumeroMesa().toString());

        String datosQR = url + value;
        String nombreFichero = "qr" + mesaDTO.getNumeroMesa();
        String extension ="png";
        ficheroService.generarQR(datosQR, AppConstants.URL_IMAGENES_MESAS_QR, nombreFichero, extension);
        mesaDTO.setImagenQR(nombreFichero + "." + extension);

        Mesa mesa = mesaDTO.convertirDTOaModelo();
        mesaRepository.save(mesa);
    }

    @Override
    public void borrarMesa(Integer id) throws MesaException {
        log.info("borrarMesa: "+id);
        Long countById = mesaRepository.countById(id);
        if (countById == null || countById == 0) {
            throw new MesaException(MesaException.NO_EXISTE);
        }

        if(pedidosService.tienePedidosMesa(id)) {
            throw new MesaException(MesaException.ERROR_BORRAR_TIENE_PEDIDOS);
        }

        mesaRepository.deleteById(id);
    }

    @Override
    public MesaDTO findByNumeroMesa(Integer numeroMesa) {
        Mesa mesa = mesaRepository.findByNumeroMesa(numeroMesa);
        return (mesa != null) ? mesa.convertirModeloaDTO() : null;
    }

    private List<MesaDTO> convertirDTOTodas(List<Mesa> mesas) {
        List<MesaDTO> lista = new ArrayList<>();

        for (Mesa mesa : mesas) {
            lista.add(mesa.convertirModeloaDTO());
        }
        return lista;

    }
}
