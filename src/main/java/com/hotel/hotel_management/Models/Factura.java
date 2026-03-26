package com.hotel.hotel_management.Models;

import jakarta.persistence.*;
import jakarta.validation.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name="factura")
@Getter @Setter
@AllArgsConstructor@NoArgsConstructor
@Builder
public class Factura {
@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(name = "numar_factura", nullable = false, unique = true)
    private String numarFactura;

    @NotNull
    @Column(name = "suma_totala", precision = 10, scale = 2, nullable = false)
    private BigDecimal sumaTotal;

    @Column(name = "suma_camera", precision = 10, scale = 2)
    private BigDecimal sumaCamera;

    @Column(name = "suma_servicii", precision = 10, scale = 2)
    private BigDecimal sumaServicii;

    @Column(name="emisa_la",updatable = false)
    @Builder.Default
    private LocalDateTime emisaLa = LocalDateTime.now();


    private LocalDate scadenta;

    @Enumerated(EnumType.STRING)
    @Column(name="status_plata",nullable = false)
    private StatusPlata statusPlata=StatusPlata.NEPLATITA;

    @OneToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="rezervare_id",nullable=false,unique=true)
    private Rezervare rezervare;

    public enum StatusPlata{
        NEPLATITA,PLATITA,ANULATA
    }
}
