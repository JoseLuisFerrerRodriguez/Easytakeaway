package com.proyecto.easytakeaway.modelos;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Entity
@Table(name = "Envios")
public class Envio {

    @Id
    @Column(name = "pedidoId", nullable = false)
    private Integer pedidoId;

    @Column(name = "direccion")
    private String direccion;

    @Column(name = "ciudad")
    private String ciudad;

    @Column(name = "codigopostal")
    private String codigoPostal;

    @MapsId
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "pedidoID")
    @ToString.Exclude
    private Pedido pedido;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado")
    private EstadoEnvio estado;


}