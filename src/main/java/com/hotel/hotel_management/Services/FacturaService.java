package com.hotel.hotel_management.Services;

import com.hotel.hotel_management.Exception.ResourceNotFoundException;
import com.hotel.hotel_management.Models.Factura;
import com.hotel.hotel_management.Models.Rezervare;
import com.hotel.hotel_management.Repositories.FacturaRepository;
import com.hotel.hotel_management.Repositories.RezervareRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

@Service
@Transactional
public class FacturaService {

    private static final Logger log = LoggerFactory.getLogger(FacturaService.class);
    private final FacturaRepository facturaRepository;
    private final RezervareRepository rezervareRepository;

    public FacturaService(FacturaRepository facturaRepository, RezervareRepository rezervareRepository) {
        this.facturaRepository = facturaRepository;
        this.rezervareRepository = rezervareRepository;
    }

    public Page<Factura> findAll(Pageable pageable) {
        log.debug("Finding all invoices, page: {}", pageable);
        return facturaRepository.findAll(pageable);
    }

    public Factura findById(Long id) {
        log.debug("Finding invoice by id: {}", id);
        return facturaRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Invoice not found with id: {}", id);
                    return new ResourceNotFoundException("Factura", id);
                });
    }

    public Optional<Factura> findByRezervareId(Long rezervareId) {
        return facturaRepository.findByRezervareId(rezervareId);
    }

    public Factura save(Factura factura) {
        if (factura.getId() == null) {
            calculeazaSumeAutomat(factura);
        }
        log.info("Saving invoice: {}", factura.getNumarFactura());
        return facturaRepository.save(factura);
    }

    public void deleteById(Long id) {
        log.info("Deleting invoice with id: {}", id);
        facturaRepository.deleteById(id);
    }

    private void calculeazaSumeAutomat(Factura factura) {
        if (factura.getRezervare() == null || factura.getRezervare().getId() == null) return;

        Rezervare rez = rezervareRepository.findByIdWithTipCamera(factura.getRezervare().getId())
                .orElse(null);
        if (rez == null || rez.getCheckIn() == null || rez.getCheckOut() == null || rez.getTipCamera() == null) return;

        long zile = ChronoUnit.DAYS.between(rez.getCheckIn(), rez.getCheckOut());
        if (zile <= 0) zile = 1;

        BigDecimal sumaCamera = rez.getTipCamera().getPret().multiply(BigDecimal.valueOf(zile));

        BigDecimal sumaServicii = rez.getRezervariServicii().stream()
                .filter(rs -> rs.getServiciu() != null && rs.getCantitate() != null && rs.getCantitate() > 0)
                .map(rs -> BigDecimal.valueOf((long) rs.getServiciu().getCost() * rs.getCantitate()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        factura.setSumaCamera(sumaCamera);
        factura.setSumaServicii(sumaServicii);
        factura.setSumaTotal(sumaCamera.add(sumaServicii));

        if (factura.getNumarFactura() == null || factura.getNumarFactura().isBlank()) {
            factura.setNumarFactura("FACT-" + LocalDate.now().getYear() + "-" + rez.getId());
        }
        if (factura.getScadenta() == null) {
            factura.setScadenta(rez.getCheckOut().plusDays(7));
        }
    }
}
