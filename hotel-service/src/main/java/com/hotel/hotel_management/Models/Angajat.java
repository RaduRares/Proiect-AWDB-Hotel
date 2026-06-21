package com.hotel.hotel_management.Models;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;

import lombok.*;

@Entity
@Table(name="angajat")
@Setter @Getter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class Angajat {
@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

@Column(nullable = false,name="nume_prenume")
private String nume_prenume;

@Column(precision = 10,scale = 2,nullable = false)
private BigDecimal salariu;

@Column(name="data_angajare", nullable = false)
private LocalDate data_angajare;

@Column(nullable = false)
@Builder.Default
private Boolean activ_munca=true;

@ManyToOne(fetch = FetchType.LAZY)
@JoinColumn(name="hotel_id",nullable = false)
private Hotel hotel;

@Column(nullable = false)
    private String functie;



}
