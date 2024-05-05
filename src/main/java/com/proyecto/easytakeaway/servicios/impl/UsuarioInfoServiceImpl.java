package com.proyecto.easytakeaway.servicios.impl;

import com.proyecto.easytakeaway.modelos.UsuarioInfo;
import com.proyecto.easytakeaway.repositorios.UsuarioInfoRepository;
import com.proyecto.easytakeaway.servicios.UsuarioInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UsuarioInfoServiceImpl implements UsuarioInfoService {
    @Autowired
    private UsuarioInfoRepository userInfoRepository;

    @Override
    public List<UsuarioInfo> getAllUserDetails() {
        return userInfoRepository.findAll();
    }

    @Override
    public void saveUserDetail(UsuarioInfo userInfo) {
        userInfoRepository.save(userInfo);
    }

    @Override
    public UsuarioInfo getUserDetail(Integer id) {
        UsuarioInfo userInfo = null;
        Optional<UsuarioInfo> optional = userInfoRepository.findById(id);
        if (optional.isPresent()) {
            userInfo = optional.get();
        }
        return userInfo;
    }

    @Override
    public void deleteUserDetail(Integer id) {
        userInfoRepository.deleteById(id);
    }
}
