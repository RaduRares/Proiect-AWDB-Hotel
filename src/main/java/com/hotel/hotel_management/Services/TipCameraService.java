package com.hotel.hotel_management.Services;

import com.hotel.hotel_management.Exception.ResourceNotFoundException;
import com.hotel.hotel_management.Models.TipCamera;
import com.hotel.hotel_management.Repositories.TipCameraRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class TipCameraService {

    private static final Logger log = LoggerFactory.getLogger(TipCameraService.class);
    private final TipCameraRepository tipCameraRepository;

    public TipCameraService(TipCameraRepository tipCameraRepository) {
        this.tipCameraRepository = tipCameraRepository;
    }

    public Page<TipCamera> findAll(Pageable pageable) {
        log.debug("Finding all room types, page: {}", pageable);
        return tipCameraRepository.findAll(pageable);
    }

    public List<TipCamera> findAll() {
        return tipCameraRepository.findAll();
    }

    public List<TipCamera> findByHotelId(Long hotelId) {
        log.debug("Finding room types for hotel: {}", hotelId);
        return tipCameraRepository.findByHotelId(hotelId);
    }

    public TipCamera findById(Long id) {
        log.debug("Finding room type by id: {}", id);
        return tipCameraRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Room type not found with id: {}", id);
                    return new ResourceNotFoundException("TipCamera", id);
                });
    }

    public TipCamera save(TipCamera tipCamera) {
        log.info("Saving room type: {}", tipCamera.getNume());
        return tipCameraRepository.save(tipCamera);
    }

    public void deleteById(Long id) {
        log.info("Deleting room type with id: {}", id);
        tipCameraRepository.deleteById(id);
    }
}
