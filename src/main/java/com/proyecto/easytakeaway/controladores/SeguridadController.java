package com.proyecto.easytakeaway.controladores;


import com.proyecto.easytakeaway.dto.UsuarioDTO;
import com.proyecto.easytakeaway.modelos.Usuario;
import com.proyecto.easytakeaway.servicios.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class SeguridadController {

    private UsuarioService usuarioService;

    public SeguridadController(UsuarioService userService) {
        this.usuarioService = userService;
    }

    @GetMapping("/accesoRestringido")
    public String accesoRestringido() {
        return "seguridad/error";
    }

    @GetMapping("/login")
    ModelAndView obtenerLogin() {
        return new ModelAndView("seguridad/login");
    }

    @PostMapping("/verificarLogin")
    public @ResponseBody String comprobarUsuario(@Param("content1") String content1, @Param("content2") String content2) {
        return usuarioService.existeUsuario(content1, content2);
    }

    @GetMapping("/registro")
    public String mostrarFormularioRegistro(Model model){
        UsuarioDTO usuario = new UsuarioDTO();
        model.addAttribute("user", usuario);
        return "seguridad/registro";
    }

    @PostMapping("/registro/guardar")
    public String registrarUsuario(@Valid @ModelAttribute("user") UsuarioDTO usuario,
                               BindingResult result,
                               Model model){
        UsuarioDTO existe = usuarioService.findByUsuario(usuario.getEmail());
        if (existe != null) {
            result.rejectValue("email", "registro.validacion.cuentaExiste", "Ya existe una cuenta registrada con ese email");
        }

        if(!usuario.getPassword().equals(usuario.getPassword2())) {
            result.rejectValue("password2", "registro.validacion.passwordNoIguales", "Las contraseñas no coinciden");
        }

        if (result.hasErrors()) {
            model.addAttribute("user", usuario);
            return "seguridad/registro";
        }
        usuarioService.registrarUsuario(usuario);
        return "redirect:/registro?success";
    }

}
