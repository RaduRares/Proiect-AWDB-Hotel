package com.hotel.hotel_management.Models;



import jakarta.persistence.*;
import lombok.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "rol")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class Rol {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String nume;

    @ManyToMany(mappedBy = "roluri")
    @Builder.Default
    private Set<User> users = new HashSet<>();
}