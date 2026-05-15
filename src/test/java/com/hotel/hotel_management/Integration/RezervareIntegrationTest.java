package com.hotel.hotel_management.Integration;

import com.hotel.hotel_management.Models.*;
import com.hotel.hotel_management.Services.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class RezervareIntegrationTest {

    @Autowired private RezervareService rezervareService;
    @Autowired private HotelService hotelService;
    @Autowired private TipCameraService tipCameraService;
    @Autowired private OaspeteService oaspeteService;

    private Hotel hotel;
    private TipCamera tipCamera;
    private Oaspete oaspete;

    @BeforeEach
    void setUp() {
        hotel = hotelService.save(Hotel.builder()
                .nume("Test Hotel")
                .stele(4)
                .adresa("Str. Test 1")
                .oras("Bucuresti")
                .build());

        tipCamera = tipCameraService.save(TipCamera.builder()
                .nume("Single Test")
                .pret(new BigDecimal("250.00"))
                .capacitate(1)
                .hotel(hotel)
                .build());

        oaspete = oaspeteService.save(Oaspete.builder()
                .nume("Test Oaspete")
                .telefon("+40720000001")
                .build());
    }

    private Rezervare buildRezervare(LocalDate checkIn, LocalDate checkOut) {
        return Rezervare.builder()
                .hotel(hotel)
                .tipCamera(tipCamera)
                .oaspete(oaspete)
                .checkIn(checkIn)
                .checkOut(checkOut)
                .nrPersoane(1)
                .status(Rezervare.StatusRezervare.CONFIRMATA)
                .build();
    }

    // --- Creare rezervare valida ---

    @Test
    void createRezervare_dateValide_success() {
        Rezervare r = buildRezervare(
                LocalDate.of(2027, 1, 10),
                LocalDate.of(2027, 1, 15));

        Rezervare saved = rezervareService.save(r);

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getCheckIn()).isEqualTo(LocalDate.of(2027, 1, 10));
        assertThat(saved.getCheckOut()).isEqualTo(LocalDate.of(2027, 1, 15));
    }

    // --- Validare date ---

    @Test
    void createRezervare_checkOutInainte_throwsException() {
        Rezervare r = buildRezervare(
                LocalDate.of(2027, 2, 15),
                LocalDate.of(2027, 2, 10)); // inainte de checkIn

        assertThatThrownBy(() -> rezervareService.save(r))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("check-out");
    }

    @Test
    void createRezervare_checkOutEgalCheckIn_throwsException() {
        LocalDate aceeaziZi = LocalDate.of(2027, 3, 1);
        Rezervare r = buildRezervare(aceeaziZi, aceeaziZi);

        assertThatThrownBy(() -> rezervareService.save(r))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("check-out");
    }

    // --- Verificare disponibilitate ---

    @Test
    void createRezervare_overlap_throwsException() {
        // Prima rezervare: 10-15 ianuarie
        Rezervare prima = buildRezervare(
                LocalDate.of(2027, 4, 10),
                LocalDate.of(2027, 4, 15));
        rezervareService.save(prima);

        // A doua rezervare: 12-18 ianuarie (overlap cu prima)
        Rezervare aDoua = buildRezervare(
                LocalDate.of(2027, 4, 12),
                LocalDate.of(2027, 4, 18));

        assertThatThrownBy(() -> rezervareService.save(aDoua))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("disponibil");
    }

    @Test
    void createRezervare_consecutiv_fara_overlap_success() {
        // Prima: 1-5 mai
        rezervareService.save(buildRezervare(
                LocalDate.of(2027, 5, 1),
                LocalDate.of(2027, 5, 5)));

        // A doua: 5-10 mai (check-in exact la check-out primul = nu overlap)
        Rezervare aDoua = buildRezervare(
                LocalDate.of(2027, 5, 5),
                LocalDate.of(2027, 5, 10));

        Rezervare saved = rezervareService.save(aDoua);
        assertThat(saved.getId()).isNotNull();
    }

    @Test
    void createRezervare_dupaCancelarea_primei_success() {
        // Prima rezervare confirmata
        Rezervare prima = buildRezervare(
                LocalDate.of(2027, 6, 1),
                LocalDate.of(2027, 6, 7));
        Rezervare savedPrima = rezervareService.save(prima);

        // Anulam prima rezervare
        savedPrima.setStatus(Rezervare.StatusRezervare.ANULATA);
        rezervareService.save(savedPrima);

        // A doua rezervare pe aceleasi date - trebuie sa mearga
        Rezervare aDoua = buildRezervare(
                LocalDate.of(2027, 6, 1),
                LocalDate.of(2027, 6, 7));

        Rezervare saved = rezervareService.save(aDoua);
        assertThat(saved.getId()).isNotNull();
    }

    @Test
    void editRezervare_fara_overlap_cuPropriulInterval_success() {
        Rezervare r = buildRezervare(
                LocalDate.of(2027, 7, 1),
                LocalDate.of(2027, 7, 5));
        Rezervare saved = rezervareService.save(r);

        // Editeaza check-out cu 1 zi mai tarziu - nu trebuie sa dea overlap cu sine
        saved.setCheckOut(LocalDate.of(2027, 7, 6));
        Rezervare updated = rezervareService.save(saved);

        assertThat(updated.getCheckOut()).isEqualTo(LocalDate.of(2027, 7, 6));
    }

    // --- Find ---

    @Test
    void findById_returnsRezervare() {
        Rezervare saved = rezervareService.save(buildRezervare(
                LocalDate.of(2027, 8, 1),
                LocalDate.of(2027, 8, 3)));

        Rezervare found = rezervareService.findById(saved.getId());

        assertThat(found.getHotel().getNume()).isEqualTo("Test Hotel");
        assertThat(found.getTipCamera().getNume()).isEqualTo("Single Test");
    }

    @Test
    void deleteRezervare_thenFindThrows() {
        Rezervare saved = rezervareService.save(buildRezervare(
                LocalDate.of(2027, 9, 1),
                LocalDate.of(2027, 9, 3)));
        Long id = saved.getId();

        rezervareService.deleteById(id);

        assertThatThrownBy(() -> rezervareService.findById(id))
                .isInstanceOf(RuntimeException.class);
    }
}
