package com.hotel.hotel_management.Services;

import com.hotel.hotel_management.Exception.ResourceNotFoundException;
import com.hotel.hotel_management.Models.Angajat;
import com.hotel.hotel_management.Repositories.AngajatRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class AngajatService {

    private static final Logger log = LoggerFactory.getLogger(AngajatService.class);
    private final AngajatRepository angajatRepository;

    public AngajatService(AngajatRepository angajatRepository) {
        this.angajatRepository = angajatRepository;
    }

    public Page<Angajat> findAll(Pageable pageable) {
        log.debug("Finding all employees, page: {}", pageable);
        return angajatRepository.findAll(pageable);
    }

    public Page<Angajat> findByHotelId(Long hotelId, Pageable pageable) {
        log.debug("Finding employees for hotel: {}", hotelId);
        return angajatRepository.findByHotelId(hotelId, pageable);
    }

    public Angajat findById(Long id) {
        log.debug("Finding employee by id: {}", id);
        return angajatRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Employee not found with id: {}", id);
                    return new ResourceNotFoundException("Angajat", id);
                });
    }

    public Angajat save(Angajat angajat) {
        log.info("Saving employee: {}", angajat.getNume_prenume());
        return angajatRepository.save(angajat);
    }

    public void deleteById(Long id) {
        log.info("Deleting employee with id: {}", id);
        angajatRepository.deleteById(id);
    }
}
