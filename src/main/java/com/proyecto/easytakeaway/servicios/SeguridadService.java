package com.proyecto.easytakeaway.servicios;

public interface SeguridadService {

    String codificarContraseña(String password);

    boolean validarContraseña(String passwordBruto, String passwordCodificada);

    String getUsuarioLogeado();

    boolean comprobarLogin();

    String codificarTexto(String texto);

    public String decodificarText(String textocifrado);
}
