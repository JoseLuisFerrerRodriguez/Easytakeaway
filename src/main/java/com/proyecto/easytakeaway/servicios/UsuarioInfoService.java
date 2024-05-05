package com.proyecto.easytakeaway.servicios;

import com.proyecto.easytakeaway.modelos.UsuarioInfo;
import java.util.List;

public interface UsuarioInfoService {

    public List<UsuarioInfo> getAllUserDetails();

    public void saveUserDetail(UsuarioInfo userInfo);

    public UsuarioInfo getUserDetail(Integer id);

    public void deleteUserDetail(Integer id);
}
