package com.proyecto.easytakeaway.excepciones;

public class UsuarioException extends Exception {

    public static final String NO_EXISTE = "admin.usuarios.noexiste";
    public static final String NO_ADMIN_USUARIO = "admin.usuarios.borrar.noadmin";
    public static final String NO_DEFAULT_USUARIO = "admin.usuarios.borrar.nodefault";
    public static final String ERROR_BORRAR_TIENE_LINEAS_PEDIDOS = "admin.usuarios.borrar.conlineaspedido";

    public UsuarioException(String message) {
        super(message);
    }

}
