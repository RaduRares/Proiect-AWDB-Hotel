package com.hotel.hotel_management.Services;

import com.hotel.hotel_management.Models.*;
import com.hotel.hotel_management.Repositories.FacturaRepository;
import com.hotel.hotel_management.Repositories.RezervareRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FacturaServiceTest {

    @Mock
    private FacturaRepository facturaRepository;

    @Mock
    private RezervareRepository rezervareRepository;

    @InjectMocks
    private FacturaService facturaService;

    private Rezervare rezervare;
    private TipCamera tipCamera;
    private Factura factura;

    @BeforeEach
    void setUp() {
        tipCamera = TipCamera.builder()
                .id(1L)
                .nume("Double Deluxe")
                .pret(new BigDecimal("300.00"))
                .capacitate(2)
                .build();

        rezervare = Rezervare.builder()
                .id(10L)
                .checkIn(LocalDate.of(2026, 7, 1))
                .checkOut(LocalDate.of(2026, 7, 4)) // 3 zile
                .tipCamera(tipCamera)
                .status(Rezervare.StatusRezervare.CONFIRMATA)
                .build();

        factura = new Factura();
        factura.setRezervare(rezervare);
        factura.setStatusPlata(Factura.StatusPlata.NEPLATITA);
    }

    // --- Auto-calcul sume la creare ---

    @Test
    void save_novaFactura_calculeazaSumaCamera() {
        // 3 zile * 300 RON = 900 RON
        when(rezervareRepository.findByIdWithTipCamera(10L)).thenReturn(Optional.of(rezervare));
        when(facturaRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Factura result = facturaService.save(factura);

        assertThat(result.getSumaCamera()).isEqualByComparingTo("900.00");
        assertThat(result.getSumaServicii()).isEqualByComparingTo("0.00");
        assertThat(result.getSumaTotal()).isEqualByComparingTo("900.00");
    }

    @Test
    void save_novaFactura_cuServicii_calculeazaSumaTotal() {
        Serviciu spa = Serviciu.builder().id(1L).nume("Spa").cost(150).build();
        RezervareServiciu rs = RezervareServiciu.builder()
                .serviciu(spa)
                .cantitate(2)
                .build();
        rezervare.getRezervariServicii().add(rs);

        when(rezervareRepository.findByIdWithTipCamera(10L)).thenReturn(Optional.of(rezervare));
        when(facturaRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Factura result = facturaService.save(factura);

        // 3 zile * 300 = 900 camera + 2 * 150 = 300 servicii = 1200 total
        assertThat(result.getSumaCamera()).isEqualByComparingTo("900.00");
        assertThat(result.getSumaServicii()).isEqualByComparingTo("300.00");
        assertThat(result.getSumaTotal()).isEqualByComparingTo("1200.00");
    }

    @Test
    void save_novaFactura_autogenerareazaNumarFactura() {
        when(rezervareRepository.findByIdWithTipCamera(10L)).thenReturn(Optional.of(rezervare));
        when(facturaRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Factura result = facturaService.save(factura);

        assertThat(result.getNumarFactura()).isNotBlank();
        assertThat(result.getNumarFactura()).contains("FACT-");
        assertThat(result.getNumarFactura()).contains("10"); // id rezervare
    }

    @Test
    void save_novaFactura_numarFacturaPredefinit_nuSuprascrieNumarul() {
        factura.setNumarFactura("FACT-CUSTOM-001");
        when(rezervareRepository.findByIdWithTipCamera(10L)).thenReturn(Optional.of(rezervare));
        when(facturaRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Factura result = facturaService.save(factura);

        assertThat(result.getNumarFactura()).isEqualTo("FACT-CUSTOM-001");
    }

    @Test
    void save_novaFactura_setezaScadentaDupaCHeckOut() {
        when(rezervareRepository.findByIdWithTipCamera(10L)).thenReturn(Optional.of(rezervare));
        when(facturaRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Factura result = facturaService.save(factura);

        assertThat(result.getScadenta()).isEqualTo(LocalDate.of(2026, 7, 11)); // checkout + 7 zile
    }

    @Test
    void save_facturaExistenta_nuRecalculeaza() {
        factura.setId(5L); // existing id
        factura.setSumaCamera(new BigDecimal("500.00"));
        factura.setSumaTotal(new BigDecimal("500.00"));
        factura.setNumarFactura("FACT-VECHE-001");
        when(facturaRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Factura result = facturaService.save(factura);

        assertThat(result.getSumaCamera()).isEqualByComparingTo("500.00");
        assertThat(result.getNumarFactura()).isEqualTo("FACT-VECHE-001");
        verify(rezervareRepository, never()).findByIdWithTipCamera(any());
    }

    // --- CRUD de baza ---

    @Test
    void findById_existingId_returnsFactura() {
        Factura f = new Factura();
        f.setId(1L);
        f.setNumarFactura("FACT-2026-001");
        when(facturaRepository.findById(1L)).thenReturn(Optional.of(f));

        Factura result = facturaService.findById(1L);

        assertThat(result.getNumarFactura()).isEqualTo("FACT-2026-001");
    }

    @Test
    void findById_nonExistingId_throwsException() {
        when(facturaRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> facturaService.findById(99L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("99");
    }

    @Test
    void findByRezervareId_returnsOptional() {
        Factura f = new Factura();
        f.setId(1L);
        when(facturaRepository.findByRezervareId(10L)).thenReturn(Optional.of(f));

        Optional<Factura> result = facturaService.findByRezervareId(10L);

        assertThat(result).isPresent();
    }

    @Test
    void findAll_returnsPage() {
        Pageable pageable = PageRequest.of(0, 10);
        Factura f = new Factura();
        Page<Factura> page = new PageImpl<>(List.of(f));
        when(facturaRepository.findAll(pageable)).thenReturn(page);

        Page<Factura> result = facturaService.findAll(pageable);

        assertThat(result.getContent()).hasSize(1);
        verify(facturaRepository).findAll(pageable);
    }

    @Test
    void deleteById_callsRepository() {
        doNothing().when(facturaRepository).deleteById(1L);

        facturaService.deleteById(1L);

        verify(facturaRepository).deleteById(1L);
    }

    @Test
    void save_novaFactura_ziMinimUna_daUnaSingurazilua() {
        // checkIn == checkOut ar trebui evitat, dar daca ajunge: min 1 zi
        rezervare.setCheckOut(LocalDate.of(2026, 7, 1)); // same as checkIn
        when(rezervareRepository.findByIdWithTipCamera(10L)).thenReturn(Optional.of(rezervare));
        when(facturaRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Factura result = facturaService.save(factura);

        // zile = 0, dar min 1 => 1 * 300 = 300
        assertThat(result.getSumaCamera()).isEqualByComparingTo("300.00");
    }
}
