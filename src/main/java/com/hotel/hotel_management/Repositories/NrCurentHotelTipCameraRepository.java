package com.hotel.hotel_management.Repositories;

import com.hotel.hotel_management.Models.NrCurentHotelTipCamera;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NrCurentHotelTipCameraRepository extends JpaRepository<NrCurentHotelTipCamera, Long> {
    List<NrCurentHotelTipCamera> findByHotelId(Long hotelId);
}
