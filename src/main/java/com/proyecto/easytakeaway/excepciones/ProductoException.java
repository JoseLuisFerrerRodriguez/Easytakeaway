package com.proyecto.easytakeaway.excepciones;

public class ProductoException extends Exception {
    public static final String ERROR_GUARDAR = "admin.productos.guardar.nook" ;
    public static final String ERROR_BORRAR = "admin.productos.borrar.nook";
    public static final String ERROR_ACTUALIZAR = "admin.productos.actualizar.nook";
    public static final String NO_EXISTE = "admin.productos.validacion.noexite";


    public ProductoException(String message) {
        super(message);
    }
}
