package com.hotel.hotel_management.Services;

import com.hotel.hotel_management.Exception.BusinessValidationException;
import com.hotel.hotel_management.Exception.ResourceNotFoundException;
import com.hotel.hotel_management.Models.Rezervare;
import com.hotel.hotel_management.Repositories.NrCurentHotelTipCameraRepository;
import com.hotel.hotel_management.Repositories.RezervareRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class RezervareService {

    private static final Logger log = LoggerFactory.getLogger(RezervareService.class);
    private final RezervareRepository rezervareRepository;
    private final NrCurentHotelTipCameraRepository nrCurentRepo;

    private static final List<Rezervare.StatusRezervare> INACTIVE_STATUSES =
            List.of(Rezervare.StatusRezervare.ANULATA, Rezervare.StatusRezervare.CHECKED_OUT);

    public RezervareService(RezervareRepository rezervareRepository,
                            NrCurentHotelTipCameraRepository nrCurentRepo) {
        this.rezervareRepository = rezervareRepository;
        this.nrCurentRepo = nrCurentRepo;
    }

    public Page<Rezervare> findAll(Pageable pageable) {
        log.debug("Finding all reservations, page: {}", pageable);
        return rezervareRepository.findAll(pageable);
    }

    public Page<Rezervare> findByStatus(Rezervare.StatusRezervare status, Pageable pageable) {
        log.debug("Finding reservations by status: {}", status);
        return rezervareRepository.findByStatus(status, pageable);
    }

    public Page<Rezervare> findByHotelId(Long hotelId, Pageable pageable) {
        log.debug("Finding reservations for hotel: {}", hotelId);
        return rezervareRepository.findByHotelId(hotelId, pageable);
    }

    public Page<Rezervare> findByHotelIdAndStatus(Long hotelId, Rezervare.StatusRezervare status, Pageable pageable) {
        log.debug("Finding reservations for hotel: {} status: {}", hotelId, status);
        return rezervareRepository.findByHotelIdAndStatus(hotelId, status, pageable);
    }

    public Rezervare findById(Long id) {
        log.debug("Finding reservation by id: {}", id);
        return rezervareRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Reservation not found with id: {}", id);
                    return new ResourceNotFoundException("Rezervare", id);
                });
    }

    public Rezervare findByIdWithTipCamera(Long id) {
        return rezervareRepository.findByIdWithTipCamera(id)
                .orElseThrow(() -> new ResourceNotFoundException("Rezervare", id));
    }

    public Rezervare save(Rezervare rezervare) {
        validateDates(rezervare);
        validateDisponibilitate(rezervare);
        log.info("Saving reservation id: {}", rezervare.getId());
        return rezervareRepository.save(rezervare);
    }

    public void deleteById(Long id) {
        log.info("Deleting reservation with id: {}", id);
        rezervareRepository.deleteById(id);
    }

    private void validateDates(Rezervare rezervare) {
        if (rezervare.getCheckIn() == null || rezervare.getCheckOut() == null) return;
        if (!rezervare.getCheckOut().isAfter(rezervare.getCheckIn())) {
            throw new BusinessValidationException("Data check-out trebuie sa fie dupa data check-in.");
        }
    }

    private void validateDisponibilitate(Rezervare rezervare) {
        if (rezervare.getTipCamera() == null || rezervare.getTipCamera().getId() == null) return;
        if (rezervare.getCheckIn() == null || rezervare.getCheckOut() == null) return;

        int numarTotal = nrCurentRepo.findByTipCameraId(rezervare.getTipCamera().getId())
                .map(n -> n.getNumarTotal())
                .orElse(0);

        if (numarTotal == 0) {
            throw new BusinessValidationException(
                    "Nu este configurat numarul de camere pentru tipul selectat. Contactati administratorul.");
        }

        long overlap;
        if (rezervare.getId() == null) {
            overlap = rezervareRepository.countOverlap(
                    rezervare.getTipCamera().getId(),
                    rezervare.getCheckIn(),
                    rezervare.getCheckOut(),
                    INACTIVE_STATUSES);
        } else {
            overlap = rezervareRepository.countOverlapExcluding(
                    rezervare.getTipCamera().getId(),
                    rezervare.getCheckIn(),
                    rezervare.getCheckOut(),
                    rezervare.getId(),
                    INACTIVE_STATUSES);
        }

        if (overlap >= numarTotal) {
            throw new BusinessValidationException(
                    "Tipul de camera selectat nu este disponibil in perioada " +
                    rezervare.getCheckIn() + " - " + rezervare.getCheckOut() +
                    ". Toate camerele (" + numarTotal + ") sunt ocupate.");
        }
    }
}
