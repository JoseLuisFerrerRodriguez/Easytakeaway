package com.proyecto.easytakeaway.servicios.impl;

import com.proyecto.easytakeaway.dto.EmailDTO;
import com.proyecto.easytakeaway.dto.PedidoDTO;
import com.proyecto.easytakeaway.dto.UsuarioDTO;
import com.proyecto.easytakeaway.servicios.EmailService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;

@Service
@Slf4j
public class EmailServiceImpl implements EmailService {

    @Autowired
    private JavaMailSender javaMailSender;

    public boolean enviarEmail(EmailDTO email) {
        try {
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();

            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, true);

            mimeMessageHelper.setFrom(email.getFrom());
            mimeMessageHelper.setTo(email.getToList());
            mimeMessageHelper.setSubject(email.getSubject());
            mimeMessageHelper.setText(email.getBody(),email.getIsHTML());

            javaMailSender.send(mimeMessage);

            log.info("Message Sent Successfully to: {}", email.getToList());
            return true;
        }  catch (Exception e) {
            log.error("sendEmail() | Error : {}", e.getMessage());
            throw new RuntimeException(e);
        }

    }


    public void enviarEmailConfirmacion(UsuarioDTO usuario, PedidoDTO pedido) throws MessagingException, UnsupportedEncodingException {

        String tipoEnvio = pedido.getTipoEnvio() == 0 ? "Recogida en el local" : "A Domicilio";
        String pagado = pedido.getPagado() ? "Pagado" : "Pendiente";

        String subject = "Pedido nº " + pedido.getId() + " registrado correctamente";
        String senderName = "EasyTakeAway";
        String mailContent = "<p><b>Número de pedido:</b> " + pedido.getId() + "</p>";
        mailContent += "<p><b>Estado del pedido:</b> " + pedido.getEstado() + "</p>";
        mailContent += "<p><b>Tipo de envio:</b> " + tipoEnvio + "</p>";

        if (pedido.getTipoEnvio() == 1) {
            mailContent += "<p><i><b>Datos de envio</b></i></p>";
            mailContent += "<p><b>Direccion:</b> " + pedido.getDireccion() + "</p>";
            mailContent += "<p><b>Ciudad:</b> " + pedido.getCiudad() + "</p>";
            mailContent += "<p><b>Codigo postal:</b> " + pedido.getCodigoPostal() + "</p>";
        }

        mailContent += "<p><b>Importe del pedido:</b> " + pedido.getImporteTotal() + "</p>";
        mailContent += "<p><b>Estado del pago:</b> " + pagado + "</p>";

        mailContent += "<hr><img src='cid:logoImage' width=150 />";

        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);

        helper.setFrom("easytakeaway.proyecto@gmail.com", senderName);
        helper.setTo(usuario.getEmail());
        helper.setSubject(subject);
        helper.setText(mailContent, true);

        ClassPathResource pathResource = new ClassPathResource("/static/img/logo.jpg");
        helper.addInline("logoImage", pathResource);
        javaMailSender.send(message);
    }

    @Override
    public void enviarEmailEnvio(UsuarioDTO usuario, PedidoDTO pedido) throws MessagingException, UnsupportedEncodingException {

        String tipoEnvio = pedido.getTipoEnvio() == 0 ? "Recogida en el local" : "A Domicilio";

        String subject = "El Pedido nº " + pedido.getId() + " acaba de ser enviado";
        String senderName = "EasyTakeAway";
        String mailContent = "<p><b>Número de pedido:</b> " + pedido.getId() + "</p>";
        mailContent += "<p><b>Estado del pedido:</b> " + pedido.getEstado() + "</p>";
        mailContent += "<p><b>Tipo de envio:</b> " + tipoEnvio + "</p>";

        if (pedido.getTipoEnvio() == 1) {
            mailContent += "<p><i><b>Datos de envio</b></i></p>";
            mailContent += "<p><b>Direccion:</b> " + pedido.getDireccion() + "</p>";
            mailContent += "<p><b>Ciudad:</b> " + pedido.getCiudad() + "</p>";
            mailContent += "<p><b>Codigo postal:</b> " + pedido.getCodigoPostal() + "</p>";
        }

        mailContent += "<br>";
        mailContent += "<p>En breve dispondras de tu pedido</p>";

        mailContent += "<hr><img src='cid:logoImage' width=150 />";

        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);

        helper.setFrom("easytakeaway.proyecto@gmail.com", senderName);
        helper.setTo(usuario.getEmail());
        helper.setSubject(subject);
        helper.setText(mailContent, true);

        ClassPathResource pathResource = new ClassPathResource("/static/img/logo.jpg");
        helper.addInline("logoImage", pathResource);
        javaMailSender.send(message);

    }

    @Override
    public void enviarEmailEntregado(UsuarioDTO usuario, PedidoDTO pedido) throws MessagingException, UnsupportedEncodingException {

        String tipoEnvio = pedido.getTipoEnvio() == 0 ? "Recogida en el local" : "A Domicilio";

        String subject = "El Pedido nº " + pedido.getId() + " ha sido entregado";
        String senderName = "EasyTakeAway";
        String mailContent = "<p><b>Número de pedido:</b> " + pedido.getId() + "</p>";
        mailContent += "<br>";
        mailContent += "<p>Ya dispones de tu pedido. Esperemos que lo disfrutes!</p>";

        mailContent += "<hr><img src='cid:logoImage' width=150 />";

        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);

        helper.setFrom("easytakeaway.proyecto@gmail.com", senderName);
        helper.setTo(usuario.getEmail());
        helper.setSubject(subject);
        helper.setText(mailContent, true);

        ClassPathResource pathResource = new ClassPathResource("/static/img/logo.jpg");
        helper.addInline("logoImage", pathResource);
        javaMailSender.send(message);

    }

}
