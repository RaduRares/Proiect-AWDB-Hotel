package com.hotel.hotel_management.Services;

import com.hotel.hotel_management.Models.Rezervare;
import com.hotel.hotel_management.Models.TipCamera;
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
class RezervareServiceTest {

    @Mock
    private RezervareRepository rezervareRepository;

    @InjectMocks
    private RezervareService rezervareService;

    private Rezervare rezervare;
    private TipCamera tipCamera;

    @BeforeEach
    void setUp() {
        tipCamera = TipCamera.builder()
                .id(1L)
                .nume("Single Standard")
                .pret(new BigDecimal("200.00"))
                .capacitate(1)
                .build();

        rezervare = Rezervare.builder()
                .id(1L)
                .checkIn(LocalDate.now())
                .checkOut(LocalDate.now().plusDays(3))
                .status(Rezervare.StatusRezervare.CONFIRMATA)
                .nrPersoane(2)
                .tipCamera(tipCamera)
                .build();
    }

    // --- CRUD de baza ---

    @Test
    void findAll_withPageable_returnsPage() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Rezervare> expectedPage = new PageImpl<>(List.of(rezervare));
        when(rezervareRepository.findAll(pageable)).thenReturn(expectedPage);

        Page<Rezervare> result = rezervareService.findAll(pageable);

        assertThat(result.getContent()).hasSize(1);
        verify(rezervareRepository).findAll(pageable);
    }

    @Test
    void findById_existingId_returnsRezervare() {
        when(rezervareRepository.findById(1L)).thenReturn(Optional.of(rezervare));

        Rezervare result = rezervareService.findById(1L);

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getStatus()).isEqualTo(Rezervare.StatusRezervare.CONFIRMATA);
    }

    @Test
    void findById_nonExistingId_throwsException() {
        when(rezervareRepository.findById(42L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> rezervareService.findById(42L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("42");
    }

    @Test
    void findByStatus_returnsFilteredPage() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Rezervare> expectedPage = new PageImpl<>(List.of(rezervare));
        when(rezervareRepository.findByStatus(Rezervare.StatusRezervare.CONFIRMATA, pageable))
                .thenReturn(expectedPage);

        Page<Rezervare> result = rezervareService.findByStatus(Rezervare.StatusRezervare.CONFIRMATA, pageable);

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getStatus()).isEqualTo(Rezervare.StatusRezervare.CONFIRMATA);
    }

    @Test
    void deleteById_callsRepository() {
        doNothing().when(rezervareRepository).deleteById(1L);

        rezervareService.deleteById(1L);

        verify(rezervareRepository).deleteById(1L);
    }

    // --- Validare date ---

    @Test
    void save_checkOutBeforeCheckIn_throwsException() {
        rezervare.setCheckIn(LocalDate.now().plusDays(5));
        rezervare.setCheckOut(LocalDate.now());

        assertThatThrownBy(() -> rezervareService.save(rezervare))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("check-out");
    }

    @Test
    void save_checkOutEqualToCheckIn_throwsException() {
        LocalDate sameDate = LocalDate.now().plusDays(2);
        rezervare.setCheckIn(sameDate);
        rezervare.setCheckOut(sameDate);

        assertThatThrownBy(() -> rezervareService.save(rezervare))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("check-out");
    }

    // --- Validare disponibilitate ---

    @Test
    void save_novaRezervare_overlapExists_throwsException() {
        rezervare.setId(null);
        when(rezervareRepository.countOverlap(eq(1L), any(), any(), any())).thenReturn(1L);

        assertThatThrownBy(() -> rezervareService.save(rezervare))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("disponibil");
    }

    @Test
    void save_novaRezervare_noOverlap_savesSuccessfully() {
        rezervare.setId(null);
        when(rezervareRepository.countOverlap(eq(1L), any(), any(), any())).thenReturn(0L);
        when(rezervareRepository.save(any())).thenReturn(rezervare);

        Rezervare result = rezervareService.save(rezervare);

        assertThat(result).isNotNull();
        verify(rezervareRepository).save(any());
        verify(rezervareRepository, never()).countOverlapExcluding(any(), any(), any(), any(), any());
    }

    @Test
    void save_editRezervare_overlapWithAltaRezervare_throwsException() {
        when(rezervareRepository.countOverlapExcluding(eq(1L), any(), any(), eq(1L), any())).thenReturn(1L);

        assertThatThrownBy(() -> rezervareService.save(rezervare))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("disponibil");
    }

    @Test
    void save_editRezervare_noOverlap_savesSuccessfully() {
        when(rezervareRepository.countOverlapExcluding(eq(1L), any(), any(), eq(1L), any())).thenReturn(0L);
        when(rezervareRepository.save(any())).thenReturn(rezervare);

        Rezervare result = rezervareService.save(rezervare);

        assertThat(result).isNotNull();
        verify(rezervareRepository, never()).countOverlap(any(), any(), any(), any());
    }

    @Test
    void save_faraTipCamera_skipAvailabilityCheck_savesSuccessfully() {
        Rezervare fara = Rezervare.builder()
                .id(null)
                .checkIn(LocalDate.now())
                .checkOut(LocalDate.now().plusDays(2))
                .status(Rezervare.StatusRezervare.CONFIRMATA)
                .build();
        when(rezervareRepository.save(any())).thenReturn(fara);

        Rezervare result = rezervareService.save(fara);

        assertThat(result).isNotNull();
        verify(rezervareRepository, never()).countOverlap(any(), any(), any(), any());
    }

    @Test
    void save_multipleZile_checkInCheckOutValid() {
        rezervare.setId(null);
        rezervare.setCheckIn(LocalDate.of(2026, 7, 1));
        rezervare.setCheckOut(LocalDate.of(2026, 7, 10));
        when(rezervareRepository.countOverlap(eq(1L), any(), any(), any())).thenReturn(0L);
        when(rezervareRepository.save(any())).thenReturn(rezervare);

        Rezervare result = rezervareService.save(rezervare);

        assertThat(result).isNotNull();
    }
}
