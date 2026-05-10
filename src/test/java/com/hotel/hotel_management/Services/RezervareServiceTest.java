package com.hotel.hotel_management.Services;

import com.hotel.hotel_management.Models.Rezervare;
import com.hotel.hotel_management.Repositories.RezervareRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RezervareServiceTest {

    @Mock
    private RezervareRepository rezervareRepository;

    @InjectMocks
    private RezervareService rezervareService;

    private Rezervare rezervare;

    @BeforeEach
    void setUp() {
        rezervare = Rezervare.builder()
                .id(1L)
                .checkIn(LocalDate.now())
                .checkOut(LocalDate.now().plusDays(3))
                .status(Rezervare.StatusRezervare.CONFIRMATA)
                .nrPersoane(2)
                .build();
    }

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
    void save_rezervare_returnsPersistedRezervare() {
        when(rezervareRepository.save(rezervare)).thenReturn(rezervare);

        Rezervare result = rezervareService.save(rezervare);

        assertThat(result).isEqualTo(rezervare);
        verify(rezervareRepository).save(rezervare);
    }

    @Test
    void deleteById_callsRepository() {
        doNothing().when(rezervareRepository).deleteById(1L);

        rezervareService.deleteById(1L);

        verify(rezervareRepository).deleteById(1L);
    }
}
