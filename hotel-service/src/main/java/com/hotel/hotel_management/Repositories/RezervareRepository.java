package com.hotel.hotel_management.Repositories;

import com.hotel.hotel_management.Models.Rezervare;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface RezervareRepository extends JpaRepository<Rezervare, Long> {

    Page<Rezervare> findByHotelId(Long hotelId, Pageable pageable);

    Page<Rezervare> findByStatus(Rezervare.StatusRezervare status, Pageable pageable);

    Page<Rezervare> findByHotelIdAndStatus(Long hotelId, Rezervare.StatusRezervare status, Pageable pageable);

    @Query("SELECT COUNT(r) FROM Rezervare r WHERE r.tipCamera.id = :tipCameraId " +
           "AND r.status NOT IN :excludedStatuses " +
           "AND r.checkIn < :checkOut AND r.checkOut > :checkIn")
    long countOverlap(@Param("tipCameraId") Long tipCameraId,
                      @Param("checkIn") LocalDate checkIn,
                      @Param("checkOut") LocalDate checkOut,
                      @Param("excludedStatuses") List<Rezervare.StatusRezervare> excludedStatuses);

    @Query("SELECT COUNT(r) FROM Rezervare r WHERE r.tipCamera.id = :tipCameraId " +
           "AND r.status NOT IN :excludedStatuses " +
           "AND r.checkIn < :checkOut AND r.checkOut > :checkIn " +
           "AND r.id <> :excludeId")
    long countOverlapExcluding(@Param("tipCameraId") Long tipCameraId,
                                @Param("checkIn") LocalDate checkIn,
                                @Param("checkOut") LocalDate checkOut,
                                @Param("excludeId") Long excludeId,
                                @Param("excludedStatuses") List<Rezervare.StatusRezervare> excludedStatuses);

    @Query("SELECT r FROM Rezervare r LEFT JOIN FETCH r.tipCamera WHERE r.id = :id")
    Optional<Rezervare> findByIdWithTipCamera(@Param("id") Long id);
}
