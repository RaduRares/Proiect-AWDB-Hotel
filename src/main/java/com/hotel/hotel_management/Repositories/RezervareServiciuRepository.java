package com.hotel.hotel_management.Repositories;

import com.hotel.hotel_management.Models.RezervareServiciu;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RezervareServiciuRepository extends JpaRepository<RezervareServiciu, Long> {
    List<RezervareServiciu> findByRezervareId(Long rezervareId);
}
