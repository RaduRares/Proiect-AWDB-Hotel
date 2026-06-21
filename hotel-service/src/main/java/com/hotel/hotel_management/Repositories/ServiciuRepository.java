package com.hotel.hotel_management.Repositories;

import com.hotel.hotel_management.Models.Serviciu;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ServiciuRepository extends JpaRepository<Serviciu, Long> {
}
