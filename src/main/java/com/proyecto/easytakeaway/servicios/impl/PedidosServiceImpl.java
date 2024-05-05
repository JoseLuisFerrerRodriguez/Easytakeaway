package com.proyecto.easytakeaway.servicios.impl;


import com.proyecto.easytakeaway.configuracion.AppConstants;
import com.proyecto.easytakeaway.dto.*;
import com.proyecto.easytakeaway.excepciones.PedidoErrorException;
import com.proyecto.easytakeaway.modelos.*;
import com.proyecto.easytakeaway.repositorios.CarritoRepository;
import com.proyecto.easytakeaway.repositorios.PedidoRepository;
import com.proyecto.easytakeaway.servicios.EmailService;
import com.proyecto.easytakeaway.servicios.FicheroService;
import com.proyecto.easytakeaway.servicios.PagoService;
import com.proyecto.easytakeaway.servicios.PedidosService;

import jakarta.mail.MessagingException;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Date;

@Slf4j
@Service
public class PedidosServiceImpl implements PedidosService {

    @Autowired
    private CarritoRepository carritoRepository;

    @Autowired
    private PedidoRepository repositorio;

    @Autowired
    private PagoService pagoService;

    @Autowired
    private FicheroService ficheroService;

    @Autowired
    private EmailService emailService;

    @Override
    public Paginacion<PedidoDTO> listarPorPagina(int numeroPagina) {

        log.info("listarPorPagina: "+numeroPagina);
        Pageable pageable = PageRequest.of(numeroPagina - 1, PEDIDOS_POR_PAGINA);

        Page<Pedido> pagina = repositorio.findAll(pageable);

        long inicio = (numeroPagina - 1) * PEDIDOS_POR_PAGINA + 1;
        long fin = inicio + PEDIDOS_POR_PAGINA - 1;

        Paginacion<PedidoDTO> paginacion = new Paginacion<PedidoDTO>();
        paginacion.setInicio(inicio);
        paginacion.setFin(fin);
        paginacion.setTotalPaginas(pagina.getTotalPages());
        paginacion.setTotalElementos(pagina.getTotalElements());
        paginacion.setElementos(convertirDTOTodas(pagina.getContent()));

        return paginacion;

    }

    @Override
    public Paginacion<PedidoDTO> listarPorPaginaAdmin(int numeroPagina, String campo, String orden, String filtro) {

        log.info("listarPorPaginaAdmin: " +numeroPagina + " " + campo + " " + orden + " " );

        Sort sort = Sort.by(campo);
        sort = orden.equals("asc") ? sort.ascending() : sort.descending();

        Pageable pageable = PageRequest.of(numeroPagina - 1, PEDIDOS_POR_PAGINA, sort);

        Page<Pedido> pagina = null;

        if(filtro != null) {
            if(filtro.equalsIgnoreCase("procesar")) {
                pagina = repositorio.buscarTodosPendienteProcesado(pageable);
            } else if(filtro.equalsIgnoreCase("enviar")) {
                pagina = repositorio.buscarTodosPendienteEnvio(pageable);
            } else if(filtro.equalsIgnoreCase("cobrar")) {
                pagina = repositorio.buscarTodosPendienteCobro(pageable);
            }

        } else {
            pagina = repositorio.findAll(pageable);
        }

        long inicio = (numeroPagina - 1) * PEDIDOS_POR_PAGINA + 1;
        long fin = inicio + PEDIDOS_POR_PAGINA - 1;

        Paginacion<PedidoDTO> paginacion = new Paginacion<PedidoDTO>();
        paginacion.setInicio(inicio);
        paginacion.setFin(fin);
        paginacion.setTotalPaginas(pagina.getTotalPages());
        paginacion.setTotalElementos(pagina.getTotalElements());
        paginacion.setElementos(convertirDTOTodas(pagina.getContent()));


        return paginacion;

    }

    @Override
    public boolean tienePedidosMesa(Integer idMesa) {
        log.info("tienePedidosMesa" + idMesa);

        Long pedidos = repositorio.countByMesa(idMesa);

        if(pedidos != null && pedidos.intValue() > 0){
            return true;
        }

        return false;
    }

    @Override
    public boolean tieneLineasPedidoUsuario(Integer idUsuario) {
        log.info("tieneLineasPedidoUsuario" + idUsuario);

        Long lineas = carritoRepository.countByUsuario(idUsuario);

        if(lineas != null && lineas.intValue() > 0){
            return true;
        }

        return false;
    }

    public List<PedidoDTO> obtenerTodosPorUsuario(UsuarioDTO usuario){
        List<PedidoDTO> pedidos = new ArrayList<PedidoDTO>();

        List<Pedido> lista = repositorio.findOrdersByUsuario(usuario.convertirDTOaModelo());

        for (Pedido pedido : lista) {
            pedidos.add(pedido.convertirModeloaDTO());
        }

        return pedidos;
    }

    @Transactional
    @Override
    public void guardarNuevoPedido(PedidoDTO nuevoPedido, UsuarioDTO usuario, List<LineaPedidoDTO> lineasPedido) throws PedidoErrorException {

        Pedido pedido = nuevoPedido.convertirDTOaModelo();
        pedido.setUsuario(usuario.convertirDTOaModelo());

        // Establecer el estado y la fecha
        pedido.setEstado(EstadoPedido.Procesado);
        Date fechaActual = new Date();
        pedido.setFecha(new Timestamp(fechaActual.getTime()));

        // Establecer el tipo de pedido según en el envio
        if(nuevoPedido.getTipoEnvio() == 0 ) {
            pedido.setEnvio(null);
        } else {
            pedido.getEnvio().setEstado(EstadoEnvio.Pendiente);
            pedido.getEnvio().setPedido(pedido);
        }

        // Volver a revisar el total
        Float total = 0f;
        for (LineaPedidoDTO lpDTO : lineasPedido) {
            total += lpDTO.getTotal();
        }

        // Verificar que el precio es el mismo que el que viene de la vista
        BigDecimal totalCalculado = new BigDecimal(BigDecimal.valueOf(total).toPlainString()).setScale(2, RoundingMode.FLOOR);
        BigDecimal totalVista = new BigDecimal(BigDecimal.valueOf(nuevoPedido.getImporteTotal()).toPlainString()).setScale(2, RoundingMode.FLOOR);

        BigDecimal diferencia = totalVista.subtract(totalCalculado);

        if (totalCalculado.compareTo(totalVista) != 0 && diferencia.compareTo(new BigDecimal(0.2)) > 0) {
            throw new PedidoErrorException("Ha ocurrido un error al tramitar el pedido.");
        }

        pedido.setImporteTotal(total);

        // PROCEDIMENTO DE PAGO
        if(nuevoPedido.getTipoPago() == 1) {
            // TODO PENDIENTE HACER EL PAGO
            pedido.setPagado(true);
        } else {
            pedido.setPagado(false);
        }

        // 1.- Guardar el pedido
        repositorio.save(pedido);

        // 2.- Actualizar las lineas con el pedido y los datos.
        List<LineaPedido> listaLineasPedido = new ArrayList<>();
        for (LineaPedidoDTO lpDTO : lineasPedido) {
            lpDTO.setPedidoId(pedido.getId());
            LineaPedido lp = lpDTO.convertirDTOaModelo();
            listaLineasPedido.add(lp);
            carritoRepository.save(lp);
        }

        pedido.setLineasPedido(listaLineasPedido);

        // 3.- Actualizar el dto de pedido
        nuevoPedido.setId(pedido.getId());
        nuevoPedido.setEstado(pedido.getEstado());
        nuevoPedido.setFecha(pedido.getFecha());
        nuevoPedido.setTipoPago(pedido.getTipoPago());
        nuevoPedido.setImporteTotal(pedido.getImporteTotal());
        nuevoPedido.setPagado(pedido.getPagado());
        if(pedido.getEnvio()!=null)
            nuevoPedido.setEstadoEnvio(pedido.getEnvio().getEstado());

        // 4.- Generar el ticket
        ficheroService.generarTicketPDF(AppConstants.URL_TICKETS, pedido.getId().toString(), pedido);

    }

    @Override
    public void cambiarEstado(int id, String estado) throws PedidoErrorException {

        boolean enviarEmailEnvio = false;
        boolean enviarEmailEntregado = false;

        Pedido pedido = repositorio.getReferenceById(id);

        if(estado.equalsIgnoreCase(EstadoPedido.Elaborado.name())) {
            pedido.setEstado(EstadoPedido.Elaborado);

            if(pedido.getTipoEnvio()==3) {
                pedido.setEstado(EstadoPedido.Completado);
            }
        }

        if(estado.equalsIgnoreCase(EstadoPedido.Cancelado.name())) {
            pedido.setEstado(EstadoPedido.Cancelado);

            Envio envio = pedido.getEnvio();
            if(envio != null) {
                envio.setEstado(EstadoEnvio.Cancelado);
            }
        }

        if(estado.equalsIgnoreCase("Enviado")) {
            pedido.setEstado(EstadoPedido.Enviado);

            Envio envio = pedido.getEnvio();
            if(envio != null) {
                envio.setEstado(EstadoEnvio.Enviado);
            }
            enviarEmailEnvio = true;

        }

        if(estado.equalsIgnoreCase("Entregado")) {
            pedido.setEstado(EstadoPedido.Completado);

            Envio envio = pedido.getEnvio();
            if(envio != null) {
                envio.setEstado(EstadoEnvio.Entregado);
            }
            enviarEmailEntregado = true;
        }


        if(estado.equalsIgnoreCase("Cobrado")) {
            pedido.setPagado(true);
        }

        repositorio.save(pedido);

        try {
            if(enviarEmailEnvio)
                emailService.enviarEmailEnvio(pedido.getUsuario().convertirModeloaDTO(), pedido.convertirModeloaDTO());

            if(enviarEmailEntregado)
                emailService.enviarEmailEntregado(pedido.getUsuario().convertirModeloaDTO(), pedido.convertirModeloaDTO());

        } catch (MessagingException | UnsupportedEncodingException e) {
            log.error("Error al enviar el email " +e.getMessage());
            throw new PedidoErrorException("Error al enviar el email");
        }

    }


    private List<PedidoDTO> convertirDTOTodas(List<Pedido> pedidos) {
        List<PedidoDTO> lista = new ArrayList<PedidoDTO>();

        for (Pedido pedido : pedidos) {
            lista.add(pedido.convertirModeloaDTO());
        }
        return lista;

    }

}
