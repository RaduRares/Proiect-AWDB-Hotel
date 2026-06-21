package com.hotel.hotel_management.Repositories;

import com.hotel.hotel_management.Models.Factura;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FacturaRepository extends JpaRepository<Factura, Long> {
    Optional<Factura> findByRezervareId(Long rezervareId);
}
