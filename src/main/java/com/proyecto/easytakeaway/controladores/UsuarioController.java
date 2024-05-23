package com.proyecto.easytakeaway.controladores;

import com.proyecto.easytakeaway.dto.UsuarioDTO;
import com.proyecto.easytakeaway.servicios.SeguridadService;
import com.proyecto.easytakeaway.servicios.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.webjars.NotFoundException;

import java.security.Principal;

@Controller
@RequestMapping("/perfil")
public class UsuarioController {
    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private SeguridadService seguridadService;

    @GetMapping({"/", ""})
    public String getUsuarioInfo(Principal principal, Model model) {
        if (principal != null) {
            UsuarioDTO usuario = usuarioService.findByUsuario(principal.getName());
            model.addAttribute("usuario", usuario);
            return "usuarios/perfil";
        } else {
            model.addAttribute("errorMessage", new NotFoundException("No se ha encontrado el usuario"));
            return "error";
        }
    }

    @GetMapping("/editar")
    public String editarPerfilUsuario(Principal principal, Model model) {
        UsuarioDTO usuario = usuarioService.findByUsuario(principal.getName());
        model.addAttribute("usuario", usuario);
        return "usuarios/perfil_editar";
    }

    @PostMapping("/guardar")
    public String guardarPerfilUsuario(@Valid @ModelAttribute("usuario") UsuarioDTO usuarioDTO, BindingResult result, Model model, Principal principal) {
        UsuarioDTO anterior = usuarioService.findByUsuario(usuarioDTO.getUsername());

        if (result.hasErrors()) {
            model.addAttribute("usuario", usuarioDTO);
            return "usuarios/perfil_editar";
        }

        anterior.setPassword(usuarioDTO.getPassword());
        anterior.setNombre(usuarioDTO.getNombre());
        anterior.setApellidos(usuarioDTO.getApellidos());
        anterior.setVia(usuarioDTO.getVia());
        anterior.setDireccion(usuarioDTO.getDireccion());
        anterior.setNumero(usuarioDTO.getNumero());
        anterior.setRestoDireccion(usuarioDTO.getRestoDireccion());
        anterior.setCiudad(usuarioDTO.getCiudad());
        anterior.setCodigoPostal(usuarioDTO.getCodigoPostal());
        anterior.setTelefono(usuarioDTO.getTelefono());

        usuarioService.guardarUsuario(anterior);

        return "redirect:/perfil";
    }

}
