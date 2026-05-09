package com.hotel.hotel_management.Services;

import com.hotel.hotel_management.Models.Serviciu;
import com.hotel.hotel_management.Repositories.ServiciuRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class ServiciuService {

    private static final Logger log = LoggerFactory.getLogger(ServiciuService.class);
    private final ServiciuRepository serviciuRepository;

    public ServiciuService(ServiciuRepository serviciuRepository) {
        this.serviciuRepository = serviciuRepository;
    }

    public Page<Serviciu> findAll(Pageable pageable) {
        log.debug("Finding all services, page: {}", pageable);
        return serviciuRepository.findAll(pageable);
    }

    public List<Serviciu> findAll() {
        return serviciuRepository.findAll();
    }

    public Serviciu findById(Long id) {
        log.debug("Finding service by id: {}", id);
        return serviciuRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Service not found with id: {}", id);
                    return new RuntimeException("Serviciul cu id-ul " + id + " nu a fost gasit");
                });
    }

    public Serviciu save(Serviciu serviciu) {
        log.info("Saving service: {}", serviciu.getNume());
        return serviciuRepository.save(serviciu);
    }

    public void deleteById(Long id) {
        log.info("Deleting service with id: {}", id);
        serviciuRepository.deleteById(id);
    }
}
