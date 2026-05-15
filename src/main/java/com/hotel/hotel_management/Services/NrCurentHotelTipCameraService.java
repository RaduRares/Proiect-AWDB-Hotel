package com.hotel.hotel_management.Services;

import com.hotel.hotel_management.Models.NrCurentHotelTipCamera;
import com.hotel.hotel_management.Repositories.NrCurentHotelTipCameraRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class NrCurentHotelTipCameraService {

    private static final Logger log = LoggerFactory.getLogger(NrCurentHotelTipCameraService.class);
    private final NrCurentHotelTipCameraRepository repository;

    public NrCurentHotelTipCameraService(NrCurentHotelTipCameraRepository repository) {
        this.repository = repository;
    }

    public List<NrCurentHotelTipCamera> findAll() {
        return repository.findAll();
    }

    public List<NrCurentHotelTipCamera> findByHotelId(Long hotelId) {
        log.debug("Finding room inventory for hotel: {}", hotelId);
        return repository.findByHotelId(hotelId);
    }

    public NrCurentHotelTipCamera findById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> {
                    log.error("Room inventory entry not found with id: {}", id);
                    return new RuntimeException("Inregistrarea cu id-ul " + id + " nu a fost gasita");
                });
    }

    public NrCurentHotelTipCamera save(NrCurentHotelTipCamera entry) {
        log.info("Saving room inventory entry");
        return repository.save(entry);
    }

    public void deleteById(Long id) {
        log.info("Deleting room inventory entry with id: {}", id);
        repository.deleteById(id);
    }
}
