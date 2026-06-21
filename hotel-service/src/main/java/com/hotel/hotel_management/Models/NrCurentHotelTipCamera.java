package com.hotel.hotel_management.Models;
import jakarta.persistence.*;
import jakarta.validation.*;
import jakarta.validation.constraints.Min;
import lombok.*;

@Entity
@Table(name = "nr_curent_hotel_tip_camera")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class NrCurentHotelTipCamera {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Min(value=0)
    @Column(name="numar_total")
    @Builder.Default
    private Integer numarTotal=0;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="hotel_id",nullable = false)
    private Hotel hotel;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="tip_camera_id",nullable = false)
    private TipCamera tipCamera;


}
