package com.proyecto.easytakeaway.excepciones;

public class CategoriaException extends Exception {


    public static final String NO_EXISTE = "admin.categoria.validacion.noexite";
    public static final String ERROR_GUARDAR = "admin.categoria.guardar.nook";
    public static final String ERROR_ACTUALIZAR = "admin.categoria.actualizar.nook";
    public static final String ERROR_BORRAR = "admin.categoria.borrar.nook";
    public static final String ERROR_BORRAR_EXISTE_PRODUCTOS = "admin.categoria.borrar.nook.productohijos";


    public CategoriaException(String message) {
        super(message);
    }


}
