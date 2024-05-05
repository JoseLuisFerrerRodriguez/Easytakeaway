package com.proyecto.easytakeaway.servicios.impl;


import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import com.proyecto.easytakeaway.modelos.*;
import com.proyecto.easytakeaway.servicios.FicheroService;
import com.proyecto.easytakeaway.utilidades.PDFTemplate;
import com.proyecto.easytakeaway.utilidades.QRGenerator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Slf4j
@Service
public class FicheroServiceImpl implements FicheroService {


    @Override
    public void generarQR(String datosQR, String path, String extension) {
        QRGenerator.generarQR(datosQR, path, extension);
    }

    @Override
    public String guardarImagen(String path, MultipartFile file, String nombre) throws IOException {

        String nombreFichero;

        if(nombre!=null && !nombre.isEmpty()) {
            nombreFichero = nombre;
        } else {
            nombreFichero = file.getOriginalFilename();
        }

        file.getResource();

        String filePath = path + File.separator + nombreFichero;

        File folder = new File(path);
        if(!folder.exists()) {
            folder.mkdir();
        }

        Files.copy(file.getInputStream(), Paths.get(filePath));

        return nombreFichero;
    }

    @Override
    public InputStream obtenerFichero(String path, String fileName) throws FileNotFoundException {
        String filePath = path + File.separator + fileName;

        InputStream inputStream = new FileInputStream(filePath);

        return inputStream;
    }

    public boolean borrarImagen(String path, String fileName) throws IOException {
        return borrarFichero(path, fileName);
    }

    @Override
    public boolean existeImagen(String path, String nombreFichero) {

        String filePath = path + File.separator + nombreFichero;

        File file = new File(filePath);

        if(file.exists()) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public String obtenerExtension(MultipartFile file) {
        String[] ext = file.getOriginalFilename().split("\\.");
        return ext[ext.length-1];
    }


    @Override
    public void generarTicketPDF(String path, String nombreFichero, Pedido pedido) {

        PdfWriter writer = null;
        Document documento = new Document(PageSize.A4, 20, 20, 70, 50);

        try {
            writer = PdfWriter.getInstance(documento, new FileOutputStream(path + File.separator + nombreFichero + ".pdf"));
            writer.setPageEvent(new PDFTemplate());

            String numFactura = pedido.getId().toString();

            documento.open();
            Paragraph paragraph = new Paragraph();

            String nombrecompleto = "";
            String email = "";
            String telefono = "";

            if(pedido.getUsuario()!=null && pedido.getUsuario().getUsuarioInfo()!=null) {
                UsuarioInfo datosCliente = pedido.getUsuario().getUsuarioInfo();
                nombrecompleto = datosCliente.getNombre();
                nombrecompleto += " " + datosCliente.getApellido();
                email = datosCliente.getEmail();
                telefono = datosCliente.getTelefono();
            }

            paragraph.add("\n");
            paragraph.add("Datos del cliente:" + "\n");
            paragraph.add("Nombre: " + nombrecompleto  + "\n");
            paragraph.add("Telefono: " + telefono + "\n");
            paragraph.add("Email: " + email);
            paragraph.add("\n");
            paragraph.add("\n");
            float spacAfter = 5;
            paragraph.setSpacingAfter(spacAfter);
            documento.add(paragraph);

            Phrase texto = new Phrase("PEDIDO");
            PdfPCell cabecera = new PdfPCell(texto);
            cabecera.setBackgroundColor(BaseColor.LIGHT_GRAY);
            cabecera.setBorderWidth(1);

            PdfPTable tabla = new PdfPTable(4);
            tabla.addCell("Producto");
            tabla.addCell("Cantidad");
            tabla.addCell(("IVA"));
            tabla.addCell("Precio");

            List<LineaPedido> lineasPedido = pedido.getLineasPedido();

            float importeTotalIVA10 = 0;
            float importeTotalIVA21 = 0;
            if (lineasPedido != null) {
                for (LineaPedido lp : lineasPedido) {
                    tabla.addCell(lp.getProducto().getNombre());

                    int cantidad = lp.getCantidad();
                    tabla.addCell(String.valueOf(cantidad));

                    float iva = lp.getIva() * 100;
                    tabla.addCell(String.valueOf(iva)+"%");

                    String precio = String.valueOf(lp.getTotal());
                    tabla.addCell(precio + "€");

                    if(iva==10) {
                        importeTotalIVA10 += lp.getTotal();
                    }

                    if(iva==21) {
                        importeTotalIVA21 += lp.getTotal();
                    }

                }
            }
            Float temp = importeTotalIVA10 - (importeTotalIVA10/(1.10f));
            BigDecimal iva10 = new BigDecimal(BigDecimal.valueOf(temp).toPlainString()).setScale(2, RoundingMode.FLOOR);

            temp = importeTotalIVA21 - (importeTotalIVA21/(1.21f));
            BigDecimal iva21 = new BigDecimal(BigDecimal.valueOf(temp).toPlainString()).setScale(2, RoundingMode.FLOOR);

            Float calc = importeTotalIVA10 + importeTotalIVA21 - iva10.floatValue() - iva21.floatValue();
            BigDecimal subtotal = new BigDecimal(BigDecimal.valueOf(calc).toPlainString()).setScale(2, RoundingMode.FLOOR);

            tabla.addCell("");
            tabla.addCell("");
            tabla.addCell("");
            tabla.addCell("");

            tabla.addCell("");
            tabla.addCell("");
            tabla.addCell("Subtotal");
            tabla.addCell(String.valueOf(subtotal) + "€");
            tabla.addCell("");
            tabla.addCell("");
            tabla.addCell("IVA(10%)");
            tabla.addCell(String.valueOf(iva10) + "€");
            tabla.addCell("");
            tabla.addCell("");
            tabla.addCell("IVA(21%)");
            tabla.addCell(String.valueOf(iva21) + "€");
            tabla.addCell("");
            tabla.addCell("");
            tabla.addCell("Total");
            tabla.addCell(String.valueOf(pedido.getImporteTotal()) + "€");
            documento.add(tabla);

            Paragraph paragraph2 = new Paragraph();

            paragraph2.add("\n");
            paragraph2.add("\n");
            paragraph2.add("Muchas gracias por su compra.");
            documento.add(paragraph2);

            documento.close();
            writer.close();
        } catch (FileNotFoundException | DocumentException e) {
            log.error("Error al generar el pdf. " + e.getMessage());
            e.printStackTrace();
        }
    }

    private boolean borrarFichero(String path, String fileName) throws IOException {
        Path imagePath = Path.of(path, fileName);

        if (Files.exists(imagePath)) {
            Files.delete(imagePath);
            return true;
        } else {
            return false;
        }
    }

}
