package com.proyecto.easytakeaway.excepciones;

public class MesaException extends Exception {

    public static final String NO_EXISTE = "admin.mesas.validacion.noexiste";

    public static final String ERROR_BORRAR_TIENE_PEDIDOS = "admin.mesas.borrar.conpedido";

    public MesaException(String message) {
        super(message);
    }

}
