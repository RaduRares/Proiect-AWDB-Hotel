package com.hotel.hotel_management.Repositories;

import com.hotel.hotel_management.Models.Hotel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HotelRepository extends JpaRepository<Hotel, Long> {
    Page<Hotel> findByNumeContainingIgnoreCase(String nume, Pageable pageable);
}
