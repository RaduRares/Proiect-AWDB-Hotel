package com.hotel.hotel_management.Models;


import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "rezervare_serviciu")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder

public class RezervareServiciu {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Min(value=0)
    @Column(nullable = false)
    @Builder.Default
    private Integer cantitate=0;

    @Column(name="data_folosinta")
    private LocalDate dataFolosinta;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="rezervare_id")
    private Rezervare rezervare;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="serviciu_id")
    private Serviciu serviciu;

    }

