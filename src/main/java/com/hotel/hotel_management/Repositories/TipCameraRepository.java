package com.hotel.hotel_management.Repositories;

import com.hotel.hotel_management.Models.TipCamera;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TipCameraRepository extends JpaRepository<TipCamera, Long> {
    List<TipCamera> findByHotelId(Long hotelId);
    Page<TipCamera> findByHotelId(Long hotelId, Pageable pageable);
}
