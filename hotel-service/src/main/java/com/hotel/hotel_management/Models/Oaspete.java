package com.hotel.hotel_management.Models;


import lombok.*;
import java.util.ArrayList;
import java.util.List;
import java.time.LocalDate;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;

@Entity
@Table(name="oaspepte")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class Oaspete {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nume;

    @Pattern(regexp="^[+]?[0-9]{3,16}$")
    private String telefon;

    @Column(unique = true, length = 13)
    private String cnp;

    @Column(name="data_nastere")
    private LocalDate dataNastere;

    @Column(name="data_inregistrare")
    @Builder.Default
    private LocalDate dataInregistrare = LocalDate.now();

    @OneToMany(mappedBy = "oaspete",cascade = CascadeType.ALL)
    private List<Rezervare> rezervari = new ArrayList<>();

}
