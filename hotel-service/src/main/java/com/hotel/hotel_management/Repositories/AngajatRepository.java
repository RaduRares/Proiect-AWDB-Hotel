package com.hotel.hotel_management.Repositories;

import com.hotel.hotel_management.Models.Angajat;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AngajatRepository extends JpaRepository<Angajat, Long> {
    Page<Angajat> findByHotelId(Long hotelId, Pageable pageable);
}
