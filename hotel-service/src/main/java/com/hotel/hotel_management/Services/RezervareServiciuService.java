package com.hotel.hotel_management.Services;

import com.hotel.hotel_management.Models.RezervareServiciu;
import com.hotel.hotel_management.Repositories.RezervareServiciuRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class RezervareServiciuService {

    private static final Logger log = LoggerFactory.getLogger(RezervareServiciuService.class);
    private final RezervareServiciuRepository repository;

    public RezervareServiciuService(RezervareServiciuRepository repository) {
        this.repository = repository;
    }

    public List<RezervareServiciu> findByRezervareId(Long rezervareId) {
        log.debug("Finding services for reservation: {}", rezervareId);
        return repository.findByRezervareId(rezervareId);
    }

    public RezervareServiciu findById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> {
                    log.error("RezervareServiciu not found with id: {}", id);
                    return new RuntimeException("Inregistrarea cu id-ul " + id + " nu a fost gasita");
                });
    }

    public RezervareServiciu save(RezervareServiciu rs) {
        log.info("Saving reservation-service link, reservation: {}", rs.getRezervare() != null ? rs.getRezervare().getId() : "null");
        return repository.save(rs);
    }

    public void deleteById(Long id) {
        log.info("Deleting reservation-service link with id: {}", id);
        repository.deleteById(id);
    }
}
