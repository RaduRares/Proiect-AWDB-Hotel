package com.hotel.hotel_management.Models;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;
@Entity
@Table(name="hotel")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class Hotel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message="Numele hotelului este obligatoriu")
    @Column(nullable=false)
    private String nume;

    @Min(value=1) @Max(value=5)
    private int stele;

    @NotBlank
    @Column(nullable=false)
    private String adresa;

    @NotBlank
    @Column(nullable=false)
    private String oras;

    private String telefon_rezervari;
    private String email_rezervari;

    @OneToMany(mappedBy = "hotel", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Angajat>angajati = new ArrayList<>();

    @OneToMany(mappedBy = "hotel", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Rezervare>rezervari = new ArrayList<>();

    @OneToMany(mappedBy = "hotel", cascade = CascadeType.ALL,orphanRemoval = true)
    @Builder.Default
    private List<NrCurentHotelTipCamera>tipuri_camere=new ArrayList<>();


}
