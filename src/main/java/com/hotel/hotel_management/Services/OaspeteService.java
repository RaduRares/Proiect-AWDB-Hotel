package com.hotel.hotel_management.Services;

import com.hotel.hotel_management.Exception.ResourceNotFoundException;
import com.hotel.hotel_management.Models.Oaspete;
import com.hotel.hotel_management.Repositories.OaspeteRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class OaspeteService {

    private static final Logger log = LoggerFactory.getLogger(OaspeteService.class);
    private final OaspeteRepository oaspeteRepository;

    public OaspeteService(OaspeteRepository oaspeteRepository) {
        this.oaspeteRepository = oaspeteRepository;
    }

    public Page<Oaspete> findAll(Pageable pageable) {
        log.debug("Finding all guests, page: {}", pageable);
        return oaspeteRepository.findAll(pageable);
    }

    public List<Oaspete> findAll() {
        return oaspeteRepository.findAll();
    }

    public Page<Oaspete> search(String nume, Pageable pageable) {
        log.debug("Searching guests by name: {}", nume);
        return oaspeteRepository.findByNumeContainingIgnoreCase(nume, pageable);
    }

    public Oaspete findById(Long id) {
        log.debug("Finding guest by id: {}", id);
        return oaspeteRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Guest not found with id: {}", id);
                    return new ResourceNotFoundException("Oaspete", id);
                });
    }

    public Oaspete save(Oaspete oaspete) {
        log.info("Saving guest: {}", oaspete.getNume());
        return oaspeteRepository.save(oaspete);
    }

    public void deleteById(Long id) {
        log.info("Deleting guest with id: {}", id);
        oaspeteRepository.deleteById(id);
    }
}
