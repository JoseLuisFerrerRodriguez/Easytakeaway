package com.proyecto.easytakeaway.utilidades;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import com.itextpdf.text.pdf.draw.LineSeparator;

import java.text.SimpleDateFormat;
import java.util.Date;

public class PDFTemplate extends PdfPageEventHelper {

    PdfTemplate total;

    public void onStartPage(PdfWriter writer, Document document) {

    	Phrase linea;
        Phrase imgCabecera;
        Phrase txtCabecera;
        Image imagen;
        Phrase txtFecha;
        
    	try {

	    	PdfContentByte cb = writer.getDirectContent();

    		SimpleDateFormat formateador = new SimpleDateFormat("dd/MM/yyyy");
    		String fecha = formateador.format(new Date());
    		txtFecha = new Phrase(fecha, new Font(FontFactory.getFont("Sans", 8, Font.NORMAL, BaseColor.BLACK)));
	    	ColumnText.showTextAligned(cb, Element.ALIGN_RIGHT, txtFecha, (document.right() - document.left()), document.top()+30, 0);
	    		    	
	    	//texto
    		//Agregamos un texto
    		txtCabecera = new Phrase("Factura simplificada");
	    	ColumnText.showTextAligned(cb, Element.ALIGN_RIGHT, txtCabecera, (document.right() - document.left()), document.top(), 0);
	      		    	
	    	//linea de arriba
	    	linea = new Phrase();
	    	linea.add(new LineSeparator(1, new Float(2.8), BaseColor.BLACK, Element.ALIGN_LEFT, 0));
	    	ColumnText.showTextAligned(cb, Element.ALIGN_LEFT, linea, document.rightMargin(), document.top()-5, 0);
	    	
	    	//linea de abajo
	    	linea = new Phrase();
	    	linea.add(new LineSeparator(1, new Float(2.8), BaseColor.BLACK, Element.ALIGN_LEFT, 0));
	    	ColumnText.showTextAligned(cb, Element.ALIGN_LEFT, linea, document.rightMargin(), document.top()-730, 0);

			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
      
    }

    public void onEndPage(PdfWriter writer, Document document) {
        PdfContentByte cb = writer.getDirectContent();

        Phrase pie = new Phrase(String.format("Página %d", writer.getCurrentPageNumber()));

        ColumnText.showTextAligned(cb, Element.ALIGN_CENTER, pie, (document.right() - document.left()) / 2 + document.leftMargin(), document.bottom()-20, 0);  
    } 
    
}

