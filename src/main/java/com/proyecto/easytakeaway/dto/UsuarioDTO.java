package com.proyecto.easytakeaway.dto;

import com.proyecto.easytakeaway.modelos.Rol;
import com.proyecto.easytakeaway.modelos.Usuario;
import com.proyecto.easytakeaway.modelos.UsuarioInfo;
import jakarta.persistence.Column;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioDTO {

    private Integer id;

    private String username;

    @NotEmpty
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[#$@!%&*?])[A-Za-z\\d#$@!%&*?]{8,}$",
            message="{registro.validacion.password}")
    private String password;

    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[#$@!%&*?])[A-Za-z\\d#$@!%&*?]{8,}$",
            message="{registro.validacion.password}")
    private String password2;

    @Size(max = 50)
    @NotEmpty
    private String nombre;

    @Size(max = 50)
    @NotEmpty
    private String apellidos;

    @Size(max = 10)
    private String via;

    @Size(max = 50)
    private String direccion;

    @Size(max = 15)
    private String numero;

    @Size(max = 50)
    private String restoDireccion;

    @Size(max = 40)
    private String ciudad;

    @Size(max = 5)
    private String codigoPostal;

    @Size(max = 15)
    @NotEmpty
    private String telefono;

    @Size(max = 100)
    @NotEmpty
    @Email
    private String email;

    private Integer rolID;

    private String rol;

    private List<LineaPedidoDTO> lineasPedido;

    public Usuario convertirDTOaModelo() {
        Usuario usuario = new Usuario();
        usuario.setUsuarioID(this.getId());
        usuario.setLogin(this.getEmail());
        usuario.setPassword(this.getPassword());

        UsuarioInfo usuarioInfo = new UsuarioInfo();
        usuarioInfo.setUsuarioID(this.getId());
        usuarioInfo.setNombre(this.getNombre());
        usuarioInfo.setApellido(this.getApellidos());
        usuarioInfo.setVia(this.getVia());
        usuarioInfo.setDireccion(this.getDireccion());
        usuarioInfo.setNumero(this.getNumero());
        usuarioInfo.setRestoDireccion(this.getRestoDireccion());
        usuarioInfo.setCiudad(this.getCiudad());
        usuarioInfo.setCodigoPostal(this.getCodigoPostal());
        usuarioInfo.setTelefono(this.getTelefono());
        usuarioInfo.setEmail(this.getEmail());
        usuarioInfo.setUsuario(usuario);

        Rol rol = new Rol();
        rol.setRolID(this.getRolID());
        rol.setNombre(this.getRol());

        usuario.setUsuarioInfo(usuarioInfo);
        usuario.setRol(rol);

        return usuario;
    }

}
