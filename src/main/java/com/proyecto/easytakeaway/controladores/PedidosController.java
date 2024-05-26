package com.proyecto.easytakeaway.controladores;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;
import com.proyecto.easytakeaway.configuracion.AppConstants;
import com.proyecto.easytakeaway.dto.LineaPedidoDTO;
import com.proyecto.easytakeaway.dto.PedidoDTO;
import com.proyecto.easytakeaway.dto.UsuarioDTO;
import com.proyecto.easytakeaway.excepciones.PedidoErrorException;
import com.proyecto.easytakeaway.modelos.EstadoPedido;
import com.proyecto.easytakeaway.modelos.Pedido;
import com.proyecto.easytakeaway.servicios.EmailService;
import com.proyecto.easytakeaway.servicios.PedidosService;
import com.proyecto.easytakeaway.servicios.UsuarioService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.ContentType;
import jakarta.mail.internet.MimeMessage;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.orm.jpa.JpaSystemException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.webjars.NotFoundException;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.Principal;
import java.sql.Date;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

@Controller
public class PedidosController {

    @Autowired
    private PedidosService pedidosService;

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private EmailService emailService;

    @GetMapping("/pedidos")
    public String mostrarPedidos(Model model, Principal principal) {
        if (principal != null) {
            UsuarioDTO usuario = usuarioService.findByUsuario(principal.getName());
            List<PedidoDTO> pedidos = pedidosService.obtenerTodosPorUsuario(usuario);
            model.addAttribute("pedidos", pedidos);
        } else {
            model.addAttribute("errorMessage", new NotFoundException("No se han encontrado pedidos"));
            return "error";
        }
        return "usuarios/pedidos";
    }

    @GetMapping("/procesar")
    public String crearPedido(Model model, Principal principal) {
        if (principal != null) {
            UsuarioDTO usuario = usuarioService.findByUsuario(principal.getName());
            List<LineaPedidoDTO> sinPedido = filtrarLineasSinPedido(usuario.getLineasPedido());

            PedidoDTO pedido = new PedidoDTO();
            pedido.setTipoEnvio(0);
            pedido.setTipoPago(0);

            String via = usuario.getVia()!=null?usuario.getVia()+" ":"";
            String direccion = usuario.getDireccion()!=null?usuario.getDireccion()+" ":"";
            String numero = usuario.getNumero()!=null?usuario.getNumero()+" ":"";
            String resto = usuario.getRestoDireccion()!=null?usuario.getRestoDireccion():"";

            pedido.setDireccion(via+direccion+numero+resto);
            pedido.setCiudad(usuario.getCiudad());
            pedido.setCodigoPostal(usuario.getCodigoPostal());

            model.addAttribute("pedido", pedido);
            model.addAttribute("usuario", usuario);
            model.addAttribute("lineasPedido", sinPedido);
        } else {
            model.addAttribute("errorMessage", new NotFoundException("No se han encontrado las lineas del pedido"));
            return "error";
        }
        return "usuarios/procesarPedido";
    }

    @PostMapping("/procesar")
    public String guardarPedido(PedidoDTO nuevoPedido, Principal principal, Model model, RedirectAttributes attributes) {
        UsuarioDTO usuario = usuarioService.findByUsuario(principal.getName());
        List<LineaPedidoDTO> lineasPedido = filtrarLineasSinPedido(usuario.getLineasPedido());

        // Validar
        boolean isValidated = validaryActualizarProcesoPedido(nuevoPedido, lineasPedido, model);

        if(!isValidated) {
            model.addAttribute("error", "true");
            model.addAttribute("pedido", nuevoPedido);
            model.addAttribute("usuario", usuario);
            model.addAttribute("lineasPedido", lineasPedido);
            return "usuarios/procesarPedido";
        }

        // Guardar el pedido
        try {
            pedidosService.guardarNuevoPedido(nuevoPedido, usuario, lineasPedido);
        } catch (PedidoErrorException | JpaSystemException ex) {
            model.addAttribute("error", "true");
            model.addAttribute("pedido", nuevoPedido);
            model.addAttribute("usuario", usuario);
            model.addAttribute("lineasPedido", lineasPedido);
            return "usuarios/procesarPedido";
        }

        // Enviar email de confirmacion
        try {
            emailService.enviarEmailConfirmacion(usuario, nuevoPedido);
            attributes.addFlashAttribute("message", "El pedido ha sido completado");
        } catch(MessagingException | UnsupportedEncodingException ex) {

        }

        return "redirect:/pedidos";
    }

    @RequestMapping(path = "/pedidos/descargarTicket/{id}", method = RequestMethod.GET)
    public ResponseEntity<Resource> download(@PathVariable(name = "id") int id) throws IOException {
        File file = new File(AppConstants.URL_TICKETS + File.separator + id + ".pdf");

        HttpHeaders header = new HttpHeaders();
        header.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename="+id+".pdf");
        header.add("Cache-Control", "no-cache, no-store, must-revalidate");
        header.add("Pragma", "no-cache");
        header.add("Expires", "0");

        Path path = Paths.get(file.getAbsolutePath());
        ByteArrayResource resource = new ByteArrayResource(Files.readAllBytes(path));

        return ResponseEntity.ok()
                .headers(header)
                .contentLength(file.length())
                .contentType(MediaType.parseMediaType("application/octet-stream"))
                .body(resource);
    }

    private boolean validaryActualizarProcesoPedido(PedidoDTO nuevoPedido, List<LineaPedidoDTO> lineasPedido, Model model) {
        boolean isValidated = true;

        // 1.- Que el tipo de envio y pago sean correctos
        if(nuevoPedido.getTipoEnvio() == 1 && nuevoPedido.getTipoPago() == 0) {
            isValidated = false;
            model.addAttribute("errpago", "true");
        }

        // 2.- Si el tipo de envio es a domicilio, la dirección debe estar rellenada.
        if(nuevoPedido.getTipoEnvio() == 1) {
            String direccion = nuevoPedido.getDireccion();
            String ciudad = nuevoPedido.getCiudad();
            String codigopostal = nuevoPedido.getCodigoPostal();
            if(direccion == null || direccion.isEmpty() || direccion.length() < 5) {
                isValidated = false;
                model.addAttribute("errdireccion", "true");
            }

            if(ciudad == null || ciudad.isEmpty() || ciudad.length() < 5) {
                isValidated = false;
                model.addAttribute("errciudad", "true");
            }

            if(codigopostal == null || codigopostal.isEmpty() || codigopostal.length() < 5) {
                isValidated = false;
                model.addAttribute("errcodigopostal", "true");
            }
        }

        // 3.- Que el importe total coincida
        // Calcular el total y actualizar las lineas de pedido
        Float total = 0f;
        for (LineaPedidoDTO lpDTO : lineasPedido) {
            lpDTO.setPrecioUnitario(lpDTO.getProducto().getPrecio());
            lpDTO.setIva(lpDTO.getProducto().getIva());
            lpDTO.setTotal(lpDTO.getPrecioUnitario() * lpDTO.getCantidad());

            total += lpDTO.getTotal();
        }

        BigDecimal totalCalculado = new BigDecimal(BigDecimal.valueOf(total).toPlainString()).setScale(2, RoundingMode.FLOOR);
        BigDecimal totalVista = new BigDecimal(BigDecimal.valueOf(nuevoPedido.getImporteTotal()).toPlainString()).setScale(2, RoundingMode.FLOOR);

        BigDecimal diferencia = totalVista.subtract(totalCalculado);

        if (totalCalculado.compareTo(totalVista) != 0 && diferencia.compareTo(new BigDecimal(0.2)) > 0) {
            isValidated = false;
            model.addAttribute("errimporte", "true");
        }

        return isValidated;
    }


    private List<LineaPedidoDTO> filtrarLineasSinPedido(List<LineaPedidoDTO> todasLineasPedido) {

        List<LineaPedidoDTO> sinPedido = new ArrayList<>();

        for (LineaPedidoDTO lineaPedidoDTO : todasLineasPedido) {
            if(lineaPedidoDTO.getPedidoId() == null) {
                sinPedido.add(lineaPedidoDTO);
            }
        }

        return sinPedido;

    }

}
