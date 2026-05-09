package com.hotel.hotel_management.Services;

import com.hotel.hotel_management.Models.Hotel;
import com.hotel.hotel_management.Repositories.HotelRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class HotelService {

    private static final Logger log = LoggerFactory.getLogger(HotelService.class);
    private final HotelRepository hotelRepository;

    public HotelService(HotelRepository hotelRepository) {
        this.hotelRepository = hotelRepository;
    }

    public Page<Hotel> findAll(Pageable pageable) {
        log.debug("Finding all hotels, page: {}", pageable);
        return hotelRepository.findAll(pageable);
    }

    public List<Hotel> findAll() {
        return hotelRepository.findAll();
    }

    public Page<Hotel> search(String nume, Pageable pageable) {
        log.debug("Searching hotels by name: {}", nume);
        return hotelRepository.findByNumeContainingIgnoreCase(nume, pageable);
    }

    public Hotel findById(Long id) {
        log.debug("Finding hotel by id: {}", id);
        return hotelRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Hotel not found with id: {}", id);
                    return new RuntimeException("Hotelul cu id-ul " + id + " nu a fost gasit");
                });
    }

    public Hotel save(Hotel hotel) {
        log.info("Saving hotel: {}", hotel.getNume());
        return hotelRepository.save(hotel);
    }

    public void deleteById(Long id) {
        log.info("Deleting hotel with id: {}", id);
        hotelRepository.deleteById(id);
    }
}
