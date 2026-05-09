package com.hotel.hotel_management.Services;

import com.hotel.hotel_management.Models.Factura;
import com.hotel.hotel_management.Repositories.FacturaRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
public class FacturaService {

    private static final Logger log = LoggerFactory.getLogger(FacturaService.class);
    private final FacturaRepository facturaRepository;

    public FacturaService(FacturaRepository facturaRepository) {
        this.facturaRepository = facturaRepository;
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
                    return new RuntimeException("Factura cu id-ul " + id + " nu a fost gasita");
                });
    }

    public Optional<Factura> findByRezervareId(Long rezervareId) {
        return facturaRepository.findByRezervareId(rezervareId);
    }

    public Factura save(Factura factura) {
        log.info("Saving invoice: {}", factura.getNumarFactura());
        return facturaRepository.save(factura);
    }

    public void deleteById(Long id) {
        log.info("Deleting invoice with id: {}", id);
        facturaRepository.deleteById(id);
    }
}
