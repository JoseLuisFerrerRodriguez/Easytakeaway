package com.proyecto.easytakeaway.servicios.impl;


import com.braintreegateway.BraintreeGateway;
import com.braintreegateway.ClientTokenRequest;
import com.braintreegateway.Environment;
import com.proyecto.easytakeaway.configuracion.BraintreeConfig;
import com.proyecto.easytakeaway.dto.UsuarioDTO;
import com.proyecto.easytakeaway.modelos.LineaPedido;
import com.proyecto.easytakeaway.modelos.Producto;
import com.proyecto.easytakeaway.modelos.Usuario;
import com.proyecto.easytakeaway.repositorios.CarritoRepository;
import com.proyecto.easytakeaway.repositorios.ProductoRepository;
import com.proyecto.easytakeaway.servicios.CarritoService;
import com.proyecto.easytakeaway.servicios.PagoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
public class PagoServiceImpl implements PagoService {

    @Autowired
    BraintreeConfig config;

    public BraintreeGateway getGateway() {
        return new BraintreeGateway(Environment.SANDBOX,
                config.getMerchantId(), config.getPublicKey(), config.getPrivateKey());

    }

    public String getToken() {
        ClientTokenRequest clientTokenRequest = new ClientTokenRequest();
        return getGateway().clientToken().generate(clientTokenRequest);
    }

}
