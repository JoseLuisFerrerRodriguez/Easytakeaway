package com.proyecto.easytakeaway.servicios;


import com.proyecto.easytakeaway.dto.EmailDTO;
import com.proyecto.easytakeaway.dto.PedidoDTO;
import com.proyecto.easytakeaway.dto.UsuarioDTO;
import jakarta.mail.MessagingException;

import java.io.UnsupportedEncodingException;

public interface EmailService {

    boolean enviarEmail(EmailDTO email);

    void enviarEmailConfirmacion(UsuarioDTO usuario, PedidoDTO pedido) throws MessagingException, UnsupportedEncodingException;

    void enviarEmailEnvio(UsuarioDTO usuario, PedidoDTO pedido) throws MessagingException, UnsupportedEncodingException;

    void enviarEmailEntregado(UsuarioDTO usuario, PedidoDTO pedido) throws MessagingException, UnsupportedEncodingException;
}
