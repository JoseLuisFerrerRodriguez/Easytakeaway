package com.proyecto.easytakeaway.servicios.impl;

import com.proyecto.easytakeaway.modelos.Rol;
import com.proyecto.easytakeaway.modelos.Usuario;
import com.proyecto.easytakeaway.repositorios.UsuarioRepository;
import com.proyecto.easytakeaway.servicios.SeguridadService;
import lombok.extern.slf4j.Slf4j;
import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Base64;
import java.util.Collection;
import java.util.stream.Collectors;

@Slf4j
@Service
public class SeguridadServiceImpl implements SeguridadService, UserDetailsService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private static final String PASSWORD = "descifrado";
    private static final String ALGORITHM = "PBEWithMD5AndDES";

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Usuario user = usuarioRepository.findByLogin(username);

        if (user != null) {
            Collection<Rol> roles = new ArrayList<Rol>();
            roles.add(user.getRol());

            return new org.springframework.security.core.userdetails.User(
                    user.getLogin(),
                    user.getPassword(),
                    mapRolesToAuthorities(roles));
        } else {
            throw new UsernameNotFoundException("Usuario o contraseña no encontrados.");
        }
    }

    private Collection < ? extends GrantedAuthority> mapRolesToAuthorities(Collection <Rol> roles) {
        Collection < ? extends GrantedAuthority> mapRoles = roles.stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_"+role.getNombre()))
                .collect(Collectors.toList());
        return mapRoles;
    }


    @Override
    public String codificarContraseña(String password) {
        return passwordEncoder.encode(password);
    }

    @Override
    public boolean validarContraseña(String passwordBruto, String passwordCodificada) {
        return passwordEncoder.matches(passwordBruto, passwordCodificada);
    }

    @Override
    public String getUsuarioLogeado() {
        SecurityContext securityContext = SecurityContextHolder.getContext();
        Authentication authentication = securityContext.getAuthentication();
        String userName = null;

        if (authentication != null) {
            UserDetails userDetails = (UserDetails) getPrincipal(authentication);
            userName = userDetails.getUsername();
        }
        return userName;
    }

    @Override
    public boolean comprobarLogin() {
        SecurityContext securityContext = SecurityContextHolder.getContext();
        Authentication authentication = securityContext.getAuthentication();

        return !(authentication instanceof AnonymousAuthenticationToken);
    }

    @Override
    public String codificarTexto(String textoPlano) {
        String base64Encoded = Base64.getEncoder().encodeToString(textoPlano.getBytes());

        // Es necesario quitar los caracteres /, + e = para evitar cualquier problema si va en una URL
        String textoCodificado = base64Encoded.replace("+", "-")
                                             .replace("/", "_")
                                             .replace("=", ",");
        return textoCodificado;
    }

    public String decodificarText(String textoCodificado) {
        String base64Encoded = textoCodificado.replace("-", "+")
                                              .replace("_", "/")
                                              .replace(",", "=");

        // Decodificar de Base64
        byte[] decodedBytes = Base64.getDecoder().decode(base64Encoded);
        return new String(decodedBytes);
    }

    private Object getPrincipal(Authentication authentication) {
        return authentication.getPrincipal();
    }


}
