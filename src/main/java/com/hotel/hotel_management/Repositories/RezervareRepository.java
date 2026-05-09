package com.hotel.hotel_management.Repositories;

import com.hotel.hotel_management.Models.Rezervare;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RezervareRepository extends JpaRepository<Rezervare, Long> {
    Page<Rezervare> findByHotelId(Long hotelId, Pageable pageable);
    Page<Rezervare> findByStatus(Rezervare.StatusRezervare status, Pageable pageable);
}
