package com.hotel.hotel_management.Models;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.util.List;
import java.util.ArrayList;
import lombok.*;

@Entity
@Table(name="tip_camera")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class TipCamera {
@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

@Column(nullable = false)
    private String nume;

@Column(length = 300)
    private String descriere;

@DecimalMin(value="1")
    @DecimalMax(value="999999")
    @Column(precision = 10,scale = 2,nullable = false)
    private BigDecimal pret;

@Min(value = 1)
    @Column(nullable = false)
    @Builder.Default
    private int capacitate=1;

@ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="hotel_id",nullable = false)
    private Hotel hotel;

@OneToMany(mappedBy="tipCamera",cascade = CascadeType.ALL)
    @Builder.Default
    private List<Rezervare> rezervari=new ArrayList<>();

@OneToMany(mappedBy = "tipCamera",cascade = CascadeType.ALL)
    @Builder.Default
    private List<NrCurentHotelTipCamera> nrCurentHotelTipCamere = new ArrayList<>();


}
