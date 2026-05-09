package com.hotel.hotel_management.Services;

import com.hotel.hotel_management.Models.Rezervare;
import com.hotel.hotel_management.Repositories.RezervareRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class RezervareService {

    private static final Logger log = LoggerFactory.getLogger(RezervareService.class);
    private final RezervareRepository rezervareRepository;

    public RezervareService(RezervareRepository rezervareRepository) {
        this.rezervareRepository = rezervareRepository;
    }

    public Page<Rezervare> findAll(Pageable pageable) {
        log.debug("Finding all reservations, page: {}", pageable);
        return rezervareRepository.findAll(pageable);
    }

    public Page<Rezervare> findByStatus(Rezervare.StatusRezervare status, Pageable pageable) {
        log.debug("Finding reservations by status: {}", status);
        return rezervareRepository.findByStatus(status, pageable);
    }

    public Rezervare findById(Long id) {
        log.debug("Finding reservation by id: {}", id);
        return rezervareRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Reservation not found with id: {}", id);
                    return new RuntimeException("Rezervarea cu id-ul " + id + " nu a fost gasita");
                });
    }

    public Rezervare save(Rezervare rezervare) {
        log.info("Saving reservation id: {}", rezervare.getId());
        return rezervareRepository.save(rezervare);
    }

    public void deleteById(Long id) {
        log.info("Deleting reservation with id: {}", id);
        rezervareRepository.deleteById(id);
    }
}
