package com.proyecto.easytakeaway.servicios;


import com.proyecto.easytakeaway.dto.UsuarioDTO;
import com.proyecto.easytakeaway.modelos.Pedido;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public interface FicheroService {

    void generarQR(String datosQR, String path, String nombrefichero, String extension);

    String guardarImagen(String path, MultipartFile file, String nombre) throws IOException;

    InputStream obtenerFichero(String path, String fileName) throws FileNotFoundException;

    boolean borrarImagen(String path, String imagenURL) throws IOException;

    boolean existeImagen(String path, String nombreFichero);

    String obtenerExtension(MultipartFile imagen);

    void generarTicketPDF(String path, String nombreFichero, Pedido pedido);
}
