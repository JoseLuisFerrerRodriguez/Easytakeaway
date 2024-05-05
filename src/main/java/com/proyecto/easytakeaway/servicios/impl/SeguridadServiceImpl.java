package com.proyecto.easytakeaway.servicios.impl;

import com.proyecto.easytakeaway.modelos.Rol;
import com.proyecto.easytakeaway.modelos.Usuario;
import com.proyecto.easytakeaway.repositorios.UsuarioRepository;
import com.proyecto.easytakeaway.servicios.SeguridadService;
import lombok.extern.slf4j.Slf4j;
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
import java.util.Collection;
import java.util.stream.Collectors;

@Slf4j
@Service
public class SeguridadServiceImpl implements SeguridadService, UserDetailsService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

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
    public String codificarTexto(String texto) {
        return passwordEncoder.encode(texto);
    }

    private Object getPrincipal(Authentication authentication) {
        return authentication.getPrincipal();
    }


}
