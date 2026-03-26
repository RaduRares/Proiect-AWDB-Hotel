package com.hotel.hotel_management.Models;

import jakarta.persistence.*;
import jakarta.validation.*;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.math.BigDecimal;

@Entity
@Table(name="serviciu")
@Setter@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Serviciu {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(nullable = false,unique = true)
    private String nume;

    @Column(length = 300)
    private String descriere;

    @NotNull
    @DecimalMin(value="0.0")
    @DecimalMax(value="9999999")
    private int cost;

    @Builder.Default
    private Boolean activ = true;

    @OneToMany(mappedBy = "serviciu",cascade = CascadeType.ALL)
    @Builder.Default
    private List<RezervareServiciu> rezervareServicius = new ArrayList<>();

}
