package com.hotel.hotel_management.Models;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name="rezervare")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class Rezervare {
@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

@NotNull
    @Column(name="check_in", nullable = false)
    private LocalDate checkIn;

    @NotNull
    @Column(name="check_out", nullable = false)
    private LocalDate checkOut;

@Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private StatusRezervare status=StatusRezervare.CONFIRMATA;

    @Min(value = 1)
    @Builder.Default
    private Integer nrPersoane = 1;

    @Column(length = 500)
    private String observatii;

    @Column(name="creat_la",updatable = false)
    @Builder.Default
    private LocalDateTime creatLa = LocalDateTime.now();

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="hotel_id",nullable = false)
    private Hotel hotel;

    @NotNull
    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="oaspete_id",nullable = false)
    private Oaspete oaspete;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="tip_camera",nullable = false)
    private TipCamera tipCamera;

    @OneToOne(mappedBy = "rezervare",cascade =  CascadeType.ALL, orphanRemoval = true)
    private Factura factura;

    @OneToMany(mappedBy = "rezervare", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<RezervareServiciu> rezervariServicii = new ArrayList<>();

    public enum StatusRezervare {
        CONFIRMATA, IN_ASTEPTARE, CHECKED_IN, CHECKED_OUT, ANULATA
    }


}

